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
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.internal.lambdas.SerializableLambdas
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar

const val GENERATOR_DEPENDENCY_CONFIG_NAME = "maGenerator"
const val DSL_EXTENSION_NAME = "montiarc"
const val MONTIARC_SOURCES_BASE_CLASSIFIER = "montiarcSources"
const val MONTIARC_SYMBOLS_BASE_CLASSIFIER = "arcSymbols"
const val MONTIARC_API_SYMBOL_USAGE = "montiarc-api"

const val MA_TOOL_CLASS = "montiarc.generator.MontiArcTool"

const val INTERNAL_GENERATOR_PROJECT_REF = ":generators:ma2java"
const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:ma2java"

const val INTERNAL_RTE_PROJECT_REF = ":libraries:majava-rte"
const val MAVEN_RTE_PROJECT_REF = "montiarc.libraries:majava-rte"

/**
 * Adds a sourceset entry for Montiarc models and tasks that generate java code from Montiarc models.
 * @see MontiarcCompile
 */
@Suppress("unused")
class MontiarcPlugin : Plugin<Project> {

  private lateinit var project: Project
  private lateinit var maExtension: MAExtension

  override fun apply(project: Project){
    this.project = project
    this.maExtension = project.extensions.create(DSL_EXTENSION_NAME, MAExtension::class.java)

    with (project) {
      pluginManager.apply("java-base")

      addGeneratorDependency()

      getSourceSetsOf(project).all { sourceSet ->
        // Enabling the declaration of model dependencies and prepare their extraction
        val dependencyConfig = setUpMontiarcDependencyConfiguration(sourceSet)
        setUpMontiarcSymbolDependencyConfiguration(sourceSet, dependencyConfig)

        // Adding Montiarc to all source sets and creating compile tasks from them
        addMontiarcToSourceSet(sourceSet)
        createCompileMontiarcTask(sourceSet)

        addRuntimeEnvironmentDependencyFor(sourceSet)
      }

      // Special treatments for the main and test source sets. They only exist, if the java plugin is applied
      pluginManager.withPlugin("java") {
        linkMainModelsToTestModels()
        addModelsPublicationForMain()
      }

      pluginManager.withPlugin("cd2pojo") {
        pluginManager.apply(Cd2PojoOutputAsMACompileInputPlugin::class.java)
      }

      pluginManager.apply(Cd2PojoDependencies4MAPlugin::class.java)
    }
  }

  private fun getSourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  private fun addGeneratorDependency() = with (project) {
    // Add a configuration to store the classpath for executing the ma2java generator
    configurations.create(GENERATOR_DEPENDENCY_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that gradle can actually find a jar artifact
      it.isCanBeConsumed = false  // We do not use this configuration for publishing
    }

    // Add a dependency on the ma2java jar. Depending on what the user wishes, ma2java may be drawn from maven
    // (default), or it may be an internal project dependency. This only makes sense for us, the MontiArc
    // developers, because this way we can directly test the freshly compiled version of ma2java.
    dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, provider {
      if(maExtension.internalMontiArcTesting.get()) {
        project(INTERNAL_GENERATOR_PROJECT_REF)
      } else {
        "${MAVEN_GENERATOR_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  private fun addRuntimeEnvironmentDependencyFor(sourceSet: SourceSet) = with(project) {


    addRuntimeEnvironmentDependencyFor(sourceSet.implementationConfigurationName)

    // If the project is a library and gets consumed, the consumer must transitively consume the runtime environment,
    // too. Therefore, we want to put the dependency on the api configuration. However, the api configuration only
    // exists for the main source set. Moreover, we also want to support users of the normal java plugin that does not
    // have api configurations. It only has implementation configurations which we then alternatively use.
    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) {
        addRuntimeEnvironmentDependencyFor(sourceSet.apiConfigurationName)
      }
    }
  }

  private fun addRuntimeEnvironmentDependencyFor(configName: String) = with (project) {
    // Depending on what the user wishes, majava-rte may be drawn from maven (default), or it may be an internal project
    // dependency. This only makes sense for us, the MontiArc developers, because this way we can directly test the
    // freshly compiled version of majava-rte.
    dependencies.addProvider(configName, provider {
      if (maExtension.internalMontiArcTesting.get()) {
        project(INTERNAL_RTE_PROJECT_REF)
      } else {
        "${MAVEN_RTE_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  /**
   * Adds the entry "montiarc" to every source set where users can put montiarc models.
   */
  private fun addMontiarcToSourceSet(sourceSet: SourceSet) {
    val srcDirSet = sourceSet.extensions.create(
      MontiarcSourceDirectorySet::class.java, "montiarc",
      DefaultMontiarcSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("montiarc", "${sourceSet.name} montiarc source"),
      // DefaultTaskDependencyFactory.withNoAssociatedProject()  // Needed starting with Gradle v.8
    )

    // Setting default values for the SourceDirectorySet
    val destinationDir = project.layout.buildDirectory.dir("montiarc/${sourceSet.name}")
    srcDirSet.destinationDirectory.convention(destinationDir)
    srcDirSet.srcDir(project.file("src/${sourceSet.name}/montiarc"))
    srcDirSet.filter.include("**/*.arc")


    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
    // configuration cache.
    // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    val srcDirectorySetAsFileCollection = srcDirSet as FileCollection
    sourceSet.resources.exclude(SerializableLambdas.spec { el -> srcDirectorySetAsFileCollection.contains(el.file) })
    sourceSet.allSource.source(srcDirSet)
  }

  /**
   * Create a task that compiles the MontiArc sources of the specified source set.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * task is added to the java sources of the same SourceSet.
   */
  private fun createCompileMontiarcTask(sourceSet: SourceSet): TaskProvider<MontiarcCompile> = with (project) {
    val montiarcSrcDirSet = sourceSet.extensions.getByType(MontiarcSourceDirectorySet::class.java)
    val taskName = sourceSet.compileMontiarcTaskName
    val generateTask = tasks.register(taskName, MontiarcCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates java code from the Montiarc models in source set ${sourceSet.name}."

      genTask.modelPath.setFrom(montiarcSrcDirSet.sourceDirectories)
      genTask.outputDir.set(montiarcSrcDirSet.destinationDirectory)
      genTask.symbolImportDir.setFrom(
        configurations.named(sourceSet.montiarcSymbolDependencyConfigurationName)
      )

      sourceSet.java.srcDir(genTask.javaOutputDir())
      genTask.hwcPath.setFrom( provider {
        sourceSet.allJava.sourceDirectories.files
        .filter { !it.startsWith(buildDir)}
      })
    }

    sourceSet.montiarc.get().compiledBy(generateTask, MontiarcCompile::outputDir)
    setTaskOrderAfterMaGenerator(generateTask)
    tasks.named(sourceSet.compileJavaTaskName) { it.dependsOn(generateTask) }

    return generateTask
  }

  /**
   * If [MAExtension.internalMontiArcTesting] is true, then this method schedules the [MontiarcCompile] task to run
   * after the tests of the generator finished.
   */
  private fun setTaskOrderAfterMaGenerator(generateTask: TaskProvider<MontiarcCompile>) = with (project) {
    generateTask.configure { genTask ->
      if (maExtension.internalMontiArcTesting.get()) {
        genTask.mustRunAfter(project(INTERNAL_GENERATOR_PROJECT_REF).tasks.withType(Test::class.java))
      }
    }
  }

  /**
   * Creates a configuration used to declare dependencies on montiarc models and their implementation simultaneously.
   * To this end, the _implementation_ configuration of the source set extends from the created _montiarc_ configuration
   */
  private fun setUpMontiarcDependencyConfiguration(sourceSet: SourceSet): Configuration = with (project) {
    val config = configurations.maybeCreate(sourceSet.montiarcDependencyDeclarationConfigName)
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Used to declare dependencies on other montiarc projects. This will simultaneously add their " +
      "java implementation to the implementation configuration and their models to to the montiarcSymbolDependencies"

    configurations.named(sourceSet.implementationConfigurationName) { it.extendsFrom(config) }

    return config
  }

  /**
   * Creates a configuration (_montiarcSymbolDependencies_) for the given source set, containing model dependencies
   * (.arcsym etc). Only use this configuration for processing, but not to declare dependencies! Do the latter using the
   * _montiarc_ configuration from which _montiarcSymbolDependencies_ extends from to automatically adopt the
   * dependencies.
   * @param generalDependencyConfiguration The _montiarc_ configuration that is used to _declare_ the dependencies.
   */
  private fun setUpMontiarcSymbolDependencyConfiguration(
    sourceSet: SourceSet,
    generalDependencyConfiguration: Configuration): Configuration = with (project) {

    return configurations.create(sourceSet.montiarcSymbolDependencyConfigurationName) { config ->
      config.extendsFrom(generalDependencyConfiguration)
      config.isCanBeResolved = true
      config.isCanBeConsumed = false
      config.isVisible = false
      config.description = "Contains montiarc _model_ dependencies (.arcsym, etc). Only use this configuration for " +
        "processing dependencies, but not for declaring them. For declaring them, use the _montiarc_ configuration " +
        "instead."

      addMontiarcSymbolJarAttributesTo(config)
    }
  }

  /**
   * Asserts that montiarc dependencies of the project appear as transitive dependencies in the publication.
   * To this end, this method lets the `outgoingMontiarcSymbols` configuration of the given [SourceSet] extend from
   * it's `montiarcSymbolDependencies` configuration.
   * @param sourceSet the [SourceSet] whose montiarc symbols should be published and for which this method will
   *        add the transitive dependencies.
   */
  private fun linkMontiarcDependenciesToOutgoingArcSymbolsConfiguration(sourceSet: SourceSet) {
    val configs = project.configurations
    val montiarcDependencyConfig = configs.getByName(sourceSet.montiarcDependencyDeclarationConfigName)
    val outgoingArcSymbolsConfiguration = configs.getByName(sourceSet.montiarcOutgoingSymbolsConfigurationName)

    outgoingArcSymbolsConfiguration.extendsFrom(montiarcDependencyConfig)
  }

  /**
   * Makes symbols of source set `main`'s compiled models available in `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun linkMainModelsToTestModels() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    // 1) Make main's montiarc dependencies also test's montiarc dependencies
    // We only have to link the dependencies to _montiarc_ models. Their _java implementation_ dependencies are already
    // linked, as testImplementation extends implementation
    val mainModelConfig = configurations.getByName(mainSourceSet.montiarcDependencyDeclarationConfigName)
    val testModelConfig = configurations.getByName(testSourceSet.montiarcDependencyDeclarationConfigName)
    testModelConfig.extendsFrom(mainModelConfig)


    val mainCompile = tasks.named(mainSourceSet.compileMontiarcTaskName, MontiarcCompile::class.java)
    // 2) Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.compileMontiarcTaskName, MontiarcCompile::class.java) {
      it.symbolImportDir.from(mainCompile.get().symbolOutputDir())
    }
  }

  /**
   * Sets up publishing of the symbols of the compiled models of the main source set (that must exists, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied)
   */
  private fun addModelsPublicationForMain() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to create a publication for the main source set, but the " +
        "JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

    setUpPublicationFromSymbolsOfSourceSet(mainSourceSet)
  }

  /**
   * Sets up a publication for the symbols of the compiled models of the given source set. To this end, a jar task is
   * created.
   */
  private fun setUpPublicationFromSymbolsOfSourceSet(sourceSet: SourceSet) {

    val arcSymbolsConfig = createOutgoingArcSymbolsConfiguration(sourceSet)
    val arcSymbolsJarTask = createArcSymbolsJarTask(sourceSet)
    val arcSymbolsJar = jarTaskToPublishArtifact(arcSymbolsJarTask)

    setUpPublicationOf(arcSymbolsJar, arcSymbolsConfig)
    linkMontiarcDependenciesToOutgoingArcSymbolsConfiguration(sourceSet)
  }

  /**
   * Creates a consumable configuration that contains the symbols of the compiled models of the given source set.
   */
  private fun createOutgoingArcSymbolsConfiguration(sourceSet: SourceSet): Configuration {
    return project.configurations.create(sourceSet.montiarcOutgoingSymbolsConfigurationName) { config ->
      config.isCanBeConsumed = true
      config.isCanBeResolved = false
      config.description = "Symbols of the compiled models of source set ${sourceSet.name}"

      addMontiarcSymbolJarAttributesTo(config)
    }
  }

  /**
   * Creates a jar task that packages the symbols produced by compiling the models of the source set into a jar.
   */
  private fun createArcSymbolsJarTask(sourceSet: SourceSet): TaskProvider<Jar> = with (project) {

    val compileTask = tasks.named(sourceSet.compileMontiarcTaskName, MontiarcCompile::class.java)

    val arcSymbolsJarTask = tasks.register(sourceSet.montiarcSymbolsJarTaskName, Jar::class.java) { jar ->
      jar.from(compileTask.get().symbolOutputDir())
      jar.archiveClassifier.set(sourceSet.symbolsJarClassifierName)
    }

    tasks.named(BasePlugin.ASSEMBLE_TASK_NAME) { it.dependsOn(arcSymbolsJarTask) }

    return arcSymbolsJarTask
  }

  /**
   * Gets the [LazyPublishArtifact] representation of the jar tasks output.
   */
  private fun jarTaskToPublishArtifact(task: TaskProvider<Jar>): LazyPublishArtifact {
    return LazyPublishArtifact(task, project.version.toString(), (project as ProjectInternal).fileResolver)
  }

  /**
   * Sets up the publication of the jar by adding it to the [DefaultArtifactPublicationSet], setting it as outgoing
   * artifact of the given configuration, and adding the configuration to the java [SoftwareComponent].
   * Note that the _java_ component must exist (checked by whether the [org.gradle.api.plugins.JavaPlugin] is applied
   */
  private fun setUpPublicationOf(jar: PublishArtifact, outgoingConfig: Configuration) = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to create a publication, but the JavaPlugin is not applied!")
    }

    extensions
      .getByType(DefaultArtifactPublicationSet::class.java)
      .addCandidate(jar)

    (components.getByName("java") as AdhocComponentWithVariants)  // .mapToOptional results in the jar
      .addVariantsFromConfiguration(outgoingConfig) { it.mapToOptional() }  // being an optional dependency in Maven

    outgoingConfig.outgoing.artifacts.add(jar)
  }

  /**
   * Adds the gradle module attributes to the configuration that mark it as a jar that contains .arcsym models.
   */
  private fun addMontiarcSymbolJarAttributesTo(config: Configuration) = with (project) {
    config.attributes {
      it.attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
      it.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, MONTIARC_API_SYMBOL_USAGE))
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class.java, Bundling.EXTERNAL))
      it.attribute(
        LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
        objects.named(LibraryElements::class.java, LibraryElements.JAR)
      )
    }
  }
}



