/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.cd2pojo.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

const val CD2POJO_API_SYMBOL_USAGE = "cd2pojo-api"
const val CD2POJO_SYMBOLS_BASE_CLASSIFIER = "cd2pojoSymbols"

@Suppress("unused")
class Cd2PojoDistributionPlugin : Plugin<Project> {

  private lateinit var project : Project

  override fun apply(project : Project) {
    this.project = project

    with (project) {
      extensions.getByType(JavaPluginExtension::class.java)
        .sourceSets.all { sourceSet ->
          val dependencyDeclarationConfig = setUpDependencyDeclarationConfig(sourceSet)
          val incomingSymbolConfig = setUpSymbolDependencyConfig(sourceSet, dependencyDeclarationConfig)
          addDependenciesToTaskInput(sourceSet, incomingSymbolConfig)
        }

      // Special treatments for the main and test source sets. They only exist, if the java plugin is applied
      pluginManager.withPlugin("java") {
        linkMainToTestModels()
        addModelsPublicationForMain()
      }
    }
  }

  /**
   * Creates a configuration used to declare dependencies on cd2pojo models and their implementation simultaneously.
   * To this end, the _implementation_ configuration of the source set extends from the created _cd2pojo_ configuration.
   * If the java-library plugin is applied, then _api_ will also extend from _cd2pojo_
   */
  private fun setUpDependencyDeclarationConfig(sourceSet: SourceSet): Configuration = with (project) {
    val config = configurations.maybeCreate(sourceSet.cd2PojoDependencyDeclarationConfigName())
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Used to declare dependencies on other cd2pojo projects. This will simultaneously add their " +
      "java implementation to the implementation configuration and their models to to the cd2pojoSymbolDependencies"

    configurations.named(sourceSet.implementationConfigurationName).configure {
      it.extendsFrom(config)
    }

    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) {
        configurations.named(sourceSet.apiConfigurationName).configure {
          it.extendsFrom(config)
        }
      }
    }

    return config
  }

  /**
   * Creates a configuration (_cd2pojoSymbolDependencies_) for the given source set, containing model dependencies
   * (.cdsym etc). Only use this configuration for processing, but not to declare dependencies! Do the latter using the
   * _cd2pojo_ configuration from which _cd2pojoSymbolDependencies_ extends from to automatically adopt the
   * dependencies.
   * @param generalDependencyConfiguration The cd2pojo configuration that is used to _declare_ the dependencies.
   */
  private fun setUpSymbolDependencyConfig(sourceSet: SourceSet,
                                          generalDependencyConfiguration: Configuration): Configuration {

    return project.configurations.create(sourceSet.cd2PojoSymbolDependencyConfigName()) { config ->
      config.extendsFrom(generalDependencyConfiguration)
      config.isCanBeResolved = true
      config.isCanBeConsumed = false
      config.isVisible = false
      config.description = "Contains cd _model_ dependencies (.cdsym, etc). Only use this configuration for " +
        "processing dependencies, but not for declaring them. For declaring them, use the _cd2pojo_ configuration " +
        "instead."

      addCd2PojoSymbolJarAttributesTo(config)
    }
  }

  /**
   * Adds the gradle module attributes to the configuration that mark it as a jar that contains .cdsym models.
   */
  private fun addCd2PojoSymbolJarAttributesTo(config: Configuration) {
    config.attributes {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
      it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, CD2POJO_API_SYMBOL_USAGE))
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
      it.attribute(
        LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
        project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
      )
    }
  }

  /**
   * Adds the artifacts from [dependencyConfig] to the [Cd2PojoCompile.symbolImportDir] of the task that compiles
   * [sourceSet].
   */
  private fun addDependenciesToTaskInput(sourceSet: SourceSet, dependencyConfig: Configuration) = with (project) {
    val compileTask = tasks.named(sourceSet.getCompileCd2PojoTaskName(), Cd2PojoCompile::class.java)
    compileTask.configure { genTask ->
      genTask.symbolImportDir.from(dependencyConfig)
    }
  }

  /**
   * Makes symbols of source set `main`'s compiled models available in `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun linkMainToTestModels(): Unit = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    // 1) Make main's cd2pojo dependencies also test's cd2pojo dependencies
    // We only have to link the dependencies to _cd2pojo_ models. Their _java implementation_ dependencies are already
    // linked, as testImplementation extends implementation
    val mainModelConfig = configurations.getByName(mainSourceSet.cd2PojoSymbolDependencyConfigName())
    val testModelConfig = configurations.getByName(testSourceSet.cd2PojoSymbolDependencyConfigName())
    testModelConfig.extendsFrom(mainModelConfig)


    val mainCompile = tasks.named(mainSourceSet.getCompileCd2PojoTaskName(), Cd2PojoCompile::class.java)
    // 2) Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.getCompileCd2PojoTaskName(), Cd2PojoCompile::class.java) { testCompile ->
      testCompile.symbolImportDir.from(mainCompile.get().symbolOutputDir())
    }
  }

  /**
   * Sets up publishing of the symbols of the compiled models of the main source set (that must exists, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied)
   */
  private fun addModelsPublicationForMain() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to create a publication for the main source set, but the JavaPlugin is " +
        "not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

    setUpSymbolPublicationOf(mainSourceSet)
  }

  /**
   * Sets up a publication for the symbols of the compiled models of the given source set. To this end, a jar task is
   * created.
   */
  private fun setUpSymbolPublicationOf(sourceSet: SourceSet) {

    val symbolsConfig = createOutgoingSymbolsConfig(sourceSet)
    val symbolsJarTask = createSymbolsJarTask(sourceSet)
    val symbolsJar = jarTaskToPublishArtifact(symbolsJarTask)

    setUpPublicationOf(symbolsJar, symbolsConfig)
    makeIncomingDependenciesToTransitives(sourceSet)
  }

  /**
   * Creates a consumable configuration that contains the symbols of the compiled models of the given source set.
   */
  private fun createOutgoingSymbolsConfig(sourceSet: SourceSet): Configuration {
    return project.configurations.create(sourceSet.cd2pojoOutgoingSymbolsConfigName()) { config ->
      config.isCanBeConsumed = true
      config.isCanBeResolved = false
      config.description = "Symbols of the compiled cd models of source set ${sourceSet.name}"

      addCd2PojoSymbolJarAttributesTo(config)
    }
  }

  /**
   * Creates a jar task that packages the cd symbols produced by compiling the models of the source set into a jar.
   */
  private fun createSymbolsJarTask(sourceSet: SourceSet): TaskProvider<Jar> = with (project) {

    val compileTask = tasks.named(sourceSet.getCompileCd2PojoTaskName(), Cd2PojoCompile::class.java)

    val cdSymbolsJarTask = tasks.register(sourceSet.getCd2PojoSymbolsJarTaskName(), Jar::class.java) { jar ->
      jar.from(compileTask.get().symbolOutputDir())
      jar.archiveClassifier.set(sourceSet.getCdSymbolsJarClassifierName())
    }

    tasks.named(BasePlugin.ASSEMBLE_TASK_NAME).configure { it.dependsOn(cdSymbolsJarTask) }

    return cdSymbolsJarTask
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
   * Asserts that cd dependencies of the project appear as transitive dependencies in the publication.
   * To this end, this method lets the `outgoingCd2PojoSymbols` configuration of the given [SourceSet] extend from
   * it's `cd2pojoSymbolDependencies` configuration.
   * @param sourceSet the [SourceSet] whose cd symbols should be published and for which this method will
   *        add the transitive dependencies.
   */
  private fun makeIncomingDependenciesToTransitives(sourceSet: SourceSet) {
    val configs = project.configurations
    val dependencyConfig = configs.getByName(sourceSet.cd2PojoSymbolDependencyConfigName())
    val outgoingConfig = configs.getByName(sourceSet.cd2pojoOutgoingSymbolsConfigName())

    outgoingConfig.extendsFrom(dependencyConfig)
  }
}

fun SourceSet.cd2PojoDependencyDeclarationConfigName() =
  if (SourceSet.isMain(this)) {
    "cd2pojo"
  } else {
    "${this.name}Cd2pojo"
  }

fun SourceSet.cd2PojoSymbolDependencyConfigName() =
  if (SourceSet.isMain(this)) {
    "cd2pojoSymbolDependencies"
  } else {
    this.compileClasspathConfigurationName
    "${this.name}Cd2pojoSymbolDependencies"
  }

fun SourceSet.cd2pojoOutgoingSymbolsConfigName() =
  if (SourceSet.isMain(this)) {
    "cd2pojoSymbolElements"
  } else {
    "${this.name}Cd2pojoSymbolElements"
  }

fun SourceSet.getCd2PojoSymbolsJarTaskName(): String = getTaskName("cd2Pojo", "symbolsJar")

fun SourceSet.getCdSymbolsJarClassifierName() =
  if (SourceSet.isMain(this)) {
    CD2POJO_SYMBOLS_BASE_CLASSIFIER
  } else {
    "${this.name}-$CD2POJO_SYMBOLS_BASE_CLASSIFIER"
  }
