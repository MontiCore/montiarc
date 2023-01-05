/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.internal.lambdas.SerializableLambdas
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar

const val GENERATOR_DEPENDENCY_CONFIG_NAME = "maGenerator"
const val DSL_EXTENSION_NAME = "montiarc"
const val MONTIARC_SOURCES_BASE_CLASSIFIER = "montiarcSources"
const val MONTIARC_SOURCE_USAGE = "montiarc-compile"
const val MONTIARC_MAVEN_SCOPE = "montiarc-compile"

const val MA_TOOL_CLASS = "montiarc.generator.MontiArcTool"
const val INTERNAL_GENERATOR_PROJECT_REF = ":generators:ma2java"
const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:ma2java"

/**
 * Adds a sourceset entry for Montiarc models and tasks that generate java code from Montiarc models.
 * @see MontiarcCompile
 */
class MontiarcPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    addJavaBasePlugin(project)

    val extension = project.extensions.create(DSL_EXTENSION_NAME, MAExtension::class.java)
    addGeneratorDependency(project, extension)

    val sourceSets = getSourceSetsOf(project)
    sourceSets.configureEach { sourceSet ->
      // Enabling the declaration of model dependencies and prepare their extraction
      val dependencyConfig = setUpMontiarcDependencyConfiguration(sourceSet, project)
      val srcDependencyConfig = setUpMontiarcSourceDependencyConfiguration(sourceSet, dependencyConfig, project)
      val unpackModelsTask = createUnpackDependencyModelsTask(sourceSet, srcDependencyConfig, project)

      // Adding Montiarc to all source sets and creating a compile task from it
      addMontiarcToSourceSet(sourceSet, project)
      createCompileMontiarcTask(sourceSet, unpackModelsTask, project, extension)
    }

    // Special treatments for the main and test source sets. They only exist, when the java plugin is applied
    if (project.pluginManager.hasPlugin("java")) {
      linkMainModelsToTestModels(project)
      addModelsPublicationForMain(project)
    }
  }

  private fun getSourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  private fun addJavaBasePlugin(project: Project) {
    if (!project.pluginManager.hasPlugin("java-base")) {
      project.pluginManager.apply("java-base")
    }
  }

  private fun addGeneratorDependency(project: Project, extension: MAExtension) {
    // Add a configuration to store the classpath for executing the ma2java generator
    project.configurations.create(GENERATOR_DEPENDENCY_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that we can refer to it from our task (put it on the classpath)
      it.isCanBeConsumed = false  // We do not use this configuration for publishing
    }

    // Add a dependency on the ma2java jar. Depending on what the user wishes, ma2java may be drawn from maven
    // (default), or it may be an internal project dependency. This only makes sense for us, the MontiArc
    // developers, because this way we can directly test the freshly compiled version of ma2java.
    project.dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, project.provider {
      if(extension.internalMontiArcTesting.get()) {
        project.project(INTERNAL_GENERATOR_PROJECT_REF)
      } else {
        "${MAVEN_GENERATOR_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  /**
   * Adds the entry "montiarc" to every source set where users can put montiarc models.
   * Moreover, the [destinationDirectory][org.gradle.api.file.SourceDirectorySet.getDestinationDirectory] of the
   * montiarc sources is added to the java sources of the same SourceSet
   */
  private fun addMontiarcToSourceSet(sourceSet: SourceSet, project: Project) {
    val srcDirSet = sourceSet.extensions.create(
      MontiarcSourceDirectorySet::class.java, "montiarc",
      DefaultMontiarcSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("montiarc", "${sourceSet.name} montiarc source"),
      DefaultTaskDependencyFactory.withNoAssociatedProject()
    )

    val genDir = project.layout.buildDirectory.dir("montiarc/${sourceSet.name}")
    srcDirSet.destinationDirectory.convention(genDir)
    srcDirSet.srcDir(project.file("src/${sourceSet.name}/montiarc"))
    srcDirSet.filter.include("**/*.arc")


    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
    // configuration cache.
    // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    val srcDirectorySetAsFileCollection = srcDirSet as FileCollection
    sourceSet.resources.exclude(SerializableLambdas.spec { el -> srcDirectorySetAsFileCollection.contains(el.file) })
    sourceSet.allSource.source(srcDirSet)
    sourceSet.java.srcDir(srcDirSet.classesDirectory)
  }

  private fun createCompileMontiarcTask(sourceSet: SourceSet,
                                        unpackModelsTask: TaskProvider<out AbstractCopyTask>,
                                        project: Project,
                                        extension: MAExtension): TaskProvider<MontiarcCompile> {
    val montiarcSrcDirSet = sourceSet.extensions.getByType(MontiarcSourceDirectorySet::class.java)
    val taskName = sourceSet.getCompileMontiarcTaskName()
    val generateTask = project.tasks.register(taskName, MontiarcCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates java code from the Montiarc models in source set ${sourceSet.name}."

      genTask.modelPath.setFrom(montiarcSrcDirSet.sourceDirectories)
      genTask.outputDir.set(montiarcSrcDirSet.destinationDirectory)
      genTask.symbolImportDir.setFrom(project.file("${project.projectDir}/src/${sourceSet.name}/symbols"))

      genTask.hwcPath.setFrom(project.provider {
        sourceSet.allJava.sourceDirectories.files
        .filter { !it.startsWith(montiarcSrcDirSet.classesDirectory.get().asFile)}
      })

      genTask.libModels.setFrom(unpackModelsTask)
    }

    sourceSet.montiarc().get().compiledBy(generateTask, MontiarcCompile::outputDir)
    setTaskOrderAfterMaGenerator(generateTask, project, extension)
    generateTask.configure { it.dependsOn(unpackModelsTask) }
    project.tasks.named(sourceSet.compileJavaTaskName).configure { it.dependsOn(generateTask) }

    return generateTask
  }

  /**
   * If [MAExtension.internalMontiArcTesting] is true, then this method schedules the [MontiarcCompile] task to run
   * after the tests of the generator finished.
   */
  private fun setTaskOrderAfterMaGenerator(generateTask: TaskProvider<MontiarcCompile>,
                                           project: Project,
                                           config: MAExtension) {
    generateTask.configure { genTask ->
      if (config.internalMontiArcTesting.get()) {
        genTask.mustRunAfter(project.project(INTERNAL_GENERATOR_PROJECT_REF).tasks.withType(Test::class.java))
      }
    }
  }

  /**
   * Creates a configuration used to declare dependencies on montiarc models and their implementation simultanously.
   * To this end, the _implementation_ configuration of the source set extends from the created _montiarc_ configuration
   */
  private fun setUpMontiarcDependencyConfiguration(sourceSet: SourceSet, project: Project): Configuration {
    val config = project.configurations.maybeCreate(sourceSet.montiarcDependencyConfigurationName())
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Declares dependencies on other montiarc projects. This will simultaneously add their java " +
      "implementation to the implementation configuration and their models to to the montiarcSourceDependencies"

    project.configurations.named(sourceSet.implementationConfigurationName).configure {
      it.extendsFrom(config)
    }

    return config
  }

  /**
   * Creates a configuration (_montiarcSourceDependencies_) for the given source set that contains model dependencies.
   * Only use this configuration for processing, but not to declare dependencies! Do this using the _montiarc_
   * configuration from which _montiarcSourceDependencies_ extends from to automatically adopt the dependencies.
   * @param generalDependencyConfiguration The _montiarc_ configuration that is used to _declare_ the dependencies.
   */
  private fun setUpMontiarcSourceDependencyConfiguration(
    sourceSet: SourceSet,
    generalDependencyConfiguration: Configuration,
    project: Project): Configuration {

    return project.configurations.create(sourceSet.montiarcSourceDependencyConfigurationName()) { config ->
      config.extendsFrom(generalDependencyConfiguration)
      config.isCanBeResolved = true
      config.isCanBeConsumed = false
      config.isVisible = false
      config.description = "Contains montiarc _model_ dependencies. Only use this configuration for processing " +
        "dependencies, but not for declaring them. For declaring them, use the _montiarc_ configuration instead."

      addMontiarcSourcesJarAttributesTo(config, project)
    }
  }

  /**
   * Asserts that .arc model dependencies of the project appear as transitive dependencies in the publication.
   * To this end, this method lets the `outgoingMontiarcSources` configuration of the given [SourceSet] extend from
   * it's `MontiarcSourceDependencies` configuration.
   * @param sourceSet the [SourceSet] whose montiarc models should be published and for which this method will
   *        add the transitive dependencies.
   */
  private fun linkMontiarcDependenciesToOutgoingArcSourcesConfiguration(sourceSet: SourceSet, project: Project) {
    val configs = project.configurations
    val montiarcDependencyConfig = configs.getByName(sourceSet.montiarcSourceDependencyConfigurationName())
    val outgoingArcSourcesConfiguration = configs.getByName(sourceSet.montiarcOutgoingSourcesConfigurationName())

    outgoingArcSourcesConfiguration.extendsFrom(montiarcDependencyConfig)
  }

  /**
   * Makes models of source set `main` available in `test` (that must exist, checked by whether the
   * [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun linkMainModelsToTestModels(project: Project) {
    if (!project.pluginManager.hasPlugin("java")) {
      project.logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not " +
        "applied!")
    }

    val sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    // 1) Make main's model dependencies also test's model dependencies
    // We only have to link the dependencies to _montiarc_ models. Their _implementation_ dependencies are already
    // linked, as testImplementation extends implementation
    val mainModelConfig = project.configurations.getByName(mainSourceSet.montiarcSourceDependencyConfigurationName())
    val testModelConfig = project.configurations.getByName(testSourceSet.montiarcSourceDependencyConfigurationName())
    testModelConfig.extendsFrom(mainModelConfig)

    // 2) Puts main montiarc models on the lib model path of test
    project.tasks.named(testSourceSet.getCompileMontiarcTaskName(), MontiarcCompile::class.java) {
      it.libModels.from(mainSourceSet.montiarc().get().srcDirs)
    }
  }

  /**
   * Sets up publishing of models of the main source set (that must exists, checked by whether the
   * [org.gradle.api.plugins.JavaPlugin] is applied)
   */
  private fun addModelsPublicationForMain(project: Project) {
    if (!project.pluginManager.hasPlugin("java")) {
      project.logger.error("Internal error: Tried to create a publication for the main source set, but the " +
        "JavaPlugin is not applied!")
    }

    val sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

    setUpPublicationFromModelsOfSourceSet(mainSourceSet, project)
  }

  /**
   * Sets up a publication for the models of the given source set. To this end, a jar task is created.
   */
  private fun setUpPublicationFromModelsOfSourceSet(sourceSet: SourceSet, project: Project) {
    val arcSourcesConfig = createOutgoingArcSourcesConfiguration(sourceSet, project)
    val arcSourcesJarTask = createArcSourcesJarTask(sourceSet, project)
    val arcSourcesJar = jarTaskToArtifact(arcSourcesJarTask, project)

    setUpPublicationOfArcSources(arcSourcesJar, arcSourcesConfig, project)
    linkMontiarcDependenciesToOutgoingArcSourcesConfiguration(sourceSet, project)
  }

  /**
   * Creates a consumable configuration that contains the montiarc models of the given source set.
   */
  private fun createOutgoingArcSourcesConfiguration(sourceSet: SourceSet, project: Project): Configuration {
    return project.configurations.create(sourceSet.montiarcOutgoingSourcesConfigurationName()) { config ->
      config.isCanBeConsumed = true
      config.isCanBeResolved = false
      config.description = "Source models of source set ${sourceSet.name}"

      addMontiarcSourcesJarAttributesTo(config, project)
    }
  }

  /**
   * Creates a jar task that packages the montiarc models of the source set into a jar.
   */
  private fun createArcSourcesJarTask(sourceSet: SourceSet, project: Project): TaskProvider<Jar> {
    val arcSourcesJarTask = project.tasks.register(sourceSet.getMontiarcSourcesJarTaskName(), Jar::class.java) { jar ->
      jar.from(sourceSet.montiarc().get().sourceDirectories)
      jar.archiveClassifier.set(sourceSet.getSourcesJarClassifierName())
    }

    project.tasks.named(BasePlugin.ASSEMBLE_TASK_NAME).configure { it.dependsOn(arcSourcesJarTask) }

    return arcSourcesJarTask
  }

  /**
   * Gets the [LazyPublishArtifact] representation of the jar tasks output.
   */
  private fun jarTaskToArtifact(task: TaskProvider<Jar>, project: Project): LazyPublishArtifact {
    return LazyPublishArtifact(task, project.version.toString(), (project as ProjectInternal).fileResolver)
  }

  /**
   * Sets up the publication of the jar by adding it to the [DefaultArtifactPublicationSet], setting it as outgoing
   * artifact of the given configuration, and adding the configuration to the java [SoftwareComponent].
   * Note that the _java_ component must exist (checked by whether the [org.gradle.api.plugins.JavaPlugin] is applied
   */
  private fun setUpPublicationOfArcSources(jar: PublishArtifact, outgoingConfig: Configuration, project: Project) {
    if (!project.pluginManager.hasPlugin("java")) {
      project.logger.error("Internal error: Tried to create a publication, but the JavaPlugin is not applied!")
    }

    project.extensions
      .getByType(DefaultArtifactPublicationSet::class.java)
      .addCandidate(jar)

    (project.components.getByName("java") as AdhocComponentWithVariants)
      .addVariantsFromConfiguration(outgoingConfig) { it.mapToMavenScope(MONTIARC_MAVEN_SCOPE) }

    outgoingConfig.outgoing.artifacts.add(jar)
  }

  /**
   * Adds the gradle module attributes to the configuration that mark it as a jar that contains .arc models.
   */
  private fun addMontiarcSourcesJarAttributesTo(config: Configuration, project: Project) {
    config.attributes {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
      it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, MONTIARC_SOURCE_USAGE))
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
      it.attribute(
        LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
        project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
      )
    }
  }

  /**
   * Creates a task that extracts the contents of the jars from the given dependency configuration. We use this to
   * extract .sym files from jars containing our model dependencies.
   */
  private fun createUnpackDependencyModelsTask(srcSet: SourceSet,
                                               dependencyConfig: Configuration,
                                               project: Project): TaskProvider<Sync> {
    return project.tasks.register(srcSet.getUnpackMontiarcDependenciesTaskName(), Sync::class.java) { copy ->
      copy.description = "Extracts model dependency .sym files from jars so that they can be used for the " +
          "${srcSet.name} source set. (And currently, we also extract .arc files, but this will be removed in " +
          "the future.)"
      copy.dependsOn(dependencyConfig)

      copy.from(project.provider { dependencyConfig.files.map { project.zipTree(it) } })  // TODO: maybe copy from providr { configurations.incoming.artifacts.map{it.file} } -> unzip ... noo dosnt work i guess
      copy.into(getUnpackModelDependenciesDir(srcSet, project))

      copy.include("**/*.arc")
      copy.include("**/*.sym")
      copy.includeEmptyDirs = false

      copy.duplicatesStrategy = DuplicatesStrategy.FAIL
    }
  }

  private fun getUnpackModelDependenciesDir(srcSet: SourceSet, project: Project): String {
    return "${project.buildDir}/unpacked-montiarc-dependencies/${srcSet.name}"
  }
}



