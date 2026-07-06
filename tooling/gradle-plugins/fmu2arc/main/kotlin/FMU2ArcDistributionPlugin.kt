/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.fmu2arc

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
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

const val FMU2ARC_API_SYMBOL_USAGE = "fmu2arcSymbol-for-montiarc-api"
const val FMU2ARC_API_FILE_USAGE = "fmu2arc-api-file-usages"
const val FMU2ARC_SYMBOLS_BASE_CLASSIFIER = "fmu2arcSymbols"
const val FMU2ARC_FILES_BASE_CLASSIFIER = "fmu2arcFiles"

@Suppress("unused")
class FMU2ArcDistributionPlugin  : Plugin<Project> {

  private lateinit var project : Project

  override fun apply(project : Project) {
    this.project = project
    this.project.pluginManager.apply(FMU2ArcPlugin::class.java)

    with (project) {

      extensions.getByType(JavaPluginExtension::class.java)
        .sourceSets.all { sourceSet ->
          val dependencyDeclarationConfig = setUpDependencyDeclarationConfig(sourceSet)
          val incomingFileConfig = setUpFileDependencyConfig(sourceSet, dependencyDeclarationConfig)
          addDependenciesToTaskInput(sourceSet, incomingFileConfig)
        }


      // Special treatments for the main and test source sets. They only exist, if the java plugin is applied
      pluginManager.withPlugin("java") {
        linkMainToTestModels()
        addModelsPublicationForMain()
      }
    }
  }

  /**
   * Creates a configuration used to declare dependencies on fmu2arc models and their implementation simultaneously.
   * To this end, the _implementation_ configuration of the source set extends from the created _fmu2arc_ configuration.
   * If the java-library plugin is applied, then _api_ will also extend from _fmu2arc_
   */
  private fun setUpDependencyDeclarationConfig(sourceSet: SourceSet): Configuration = with (project) {
    val config = configurations.maybeCreate(sourceSet.fmu2arcDependencyDeclarationConfigName)
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Used to declare dependencies on other fmu2arc projects. This adds their java " +
        "implementation  and their .fmu models."

    configurations.named(sourceSet.implementationConfigurationName) { it.extendsFrom(config) }

    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) {
        configurations.named(sourceSet.apiConfigurationName) { it.extendsFrom(config) }
      }
    }

    return config
  }

  /**
   * Creates a configuration (_fmu2arcFileDependencies_) for the given source set, containing model dependencies
   * (.fmu files etc). Only use this configuration for processing, but not to declare dependencies! Do the latter using the
   * _fmu2arc_ configuration from which _fmu2arcFileDependencies_ extends from to automatically adopt the
   * dependencies.
   * @param generalDependencyConfiguration The fmu2arc configuration that is used to _declare_ the dependencies.
   */
  private fun setUpFileDependencyConfig(sourceSet: SourceSet, generalDependencyConfiguration: Configuration): Configuration {
    return project.configurations.create(sourceSet.fmu2arcFileDependencyConfigName) { config ->
      config.extendsFrom(generalDependencyConfiguration)
      config.isCanBeResolved = true
      config.isCanBeConsumed = false
      config.isVisible = false
      config.description = "Contains fmu model dependencies. Only use this configuration for processing, " +
          "not for declaring."

      addFMUFileJarAttributesTo(config)
    }
  }

  /**
   * Adds the artifacts from [dependencyConfig] to the [FMU2ArcCompile.modelPath] of the task that compiles
   * [sourceSet].
   */
  private fun addDependenciesToTaskInput(sourceSet: SourceSet, dependencyConfig: Configuration) = with (project) {
    val compileTask = tasks.named(sourceSet.compileFMU2ArcTaskName, FMU2ArcCompile::class.java)
    compileTask.configure { genTask ->
      genTask.modelPath.from(dependencyConfig)
    }
  }


  /**
   * Adds the gradle module attributes to the configuration that mark it as a jar that contains .arcsym models.
   */
  private fun addFMUSymbolJarAttributesTo(config: Configuration) {
    config.attributes {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
      it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, FMU2ARC_API_SYMBOL_USAGE))
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
      it.attribute(
        LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
        project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
      )
    }
  }

  /**
   * Adds the gradle module attributes to the configuration that mark it as a jar that contains .fmu models.
   */
  private fun addFMUFileJarAttributesTo(config: Configuration) {
    config.attributes {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
      it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, FMU2ARC_API_FILE_USAGE))
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
      it.attribute(
        LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
        project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
      )
    }
  }


  /**
   * Makes files of source set `main`'s compiled models available in `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun linkMainToTestModels(): Unit = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)


    val mainModelConfig = configurations.getByName(mainSourceSet.fmu2arcDependencyDeclarationConfigName)
    val testModelConfig = configurations.getByName(testSourceSet.fmu2arcDependencyDeclarationConfigName)
    testModelConfig.extendsFrom(mainModelConfig)

  }

  /**
   * Sets up publishing of the symbols and raw .fmu files of the compiled models of the main source set (that must exists, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied)
   */
  private fun addModelsPublicationForMain() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to create a publication for the main source set, but the JavaPlugin is " +
          "not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

    setUpSymbolAndFilePublicationOf(mainSourceSet)
  }

  /**
   * Sets up a publication for the symbols and .fmu files of the compiled models of the given source set. To this end, a jar task is
   * created.
   */
  private fun setUpSymbolAndFilePublicationOf(sourceSet: SourceSet) {

    val symbolsConfig = createOutgoingSymbolsConfig(sourceSet)
    val symbolsJarTask = createSymbolsJarTask(sourceSet)
    val symbolsJar = jarTaskToPublishArtifact(symbolsJarTask)

    setUpPublicationOf(symbolsJar, symbolsConfig)

    val filesConfig = createOutgoingFilesConfig(sourceSet)
    val filesJarTask = createFileJarTask(sourceSet)
    val fileJar = jarTaskToPublishArtifact(filesJarTask)

    setUpPublicationOf(fileJar, filesConfig)

    makeIncomingDependenciesToTransitives(sourceSet)
  }

  /**
   * Creates a consumable configuration that contains the symbols of the compiled models of the given source set.
   */
  private fun createOutgoingSymbolsConfig(sourceSet: SourceSet): Configuration {
    return project.configurations.create(sourceSet.fmu2arcOutgoingSymbolsConfigName) { config ->
      config.isCanBeConsumed = true
      config.isCanBeResolved = false
      config.description = "Symbols of the compiled fmu models of source set ${sourceSet.name}"

      addFMUSymbolJarAttributesTo(config)
    }
  }

  /**
   * Creates a jar task that packages the symbols produced by compiling the models of the source set into a jar.
   */
  private fun createSymbolsJarTask(sourceSet: SourceSet): TaskProvider<Jar> = with (project) {

    val compileTask = tasks.named(sourceSet.compileFMU2ArcTaskName, FMU2ArcCompile::class.java)

    val symbolsJarTask = tasks.register(sourceSet.fmu2arcSymbolsJarTaskName, Jar::class.java) { jar ->
      jar.from(compileTask.get().symbolOutputDir())
      jar.archiveClassifier.set(sourceSet.fmuSymbolsJarClassifierName)
      jar.isPreserveFileTimestamps = false
      jar.isReproducibleFileOrder = true
    }

    tasks.named(BasePlugin.ASSEMBLE_TASK_NAME) { it.dependsOn(symbolsJarTask) }

    return symbolsJarTask
  }

  /**
   * Creates a consumable configuration that contains the .fmu files of the compiled models of the given source set.
   */
  private fun createOutgoingFilesConfig(sourceSet: SourceSet): Configuration {
    return project.configurations.create(sourceSet.fmu2arcOutgoingFileConfigName) { config ->
      config.isCanBeConsumed = true
      config.isCanBeResolved = false
      config.description = ".fmu Files of the fmu models of source set ${sourceSet.name}"

      addFMUFileJarAttributesTo(config)
    }
  }

  /**
   * Creates a jar task that packages the .fmu files produced by compiling the models of the source set into a jar.
   */
  private fun createFileJarTask(sourceSet: SourceSet): TaskProvider<Jar> = with (project) {

    val compileTask = tasks.named(sourceSet.compileFMU2ArcTaskName, FMU2ArcCompile::class.java)

    val fileTaskJar = tasks.register(sourceSet.fmu2arcFilesJarTaskName, Jar::class.java) { jar ->
      jar.from(compileTask.get().modelPath)
      jar.archiveClassifier.set(sourceSet.fmuFilesJarClassifierName)
      jar.isPreserveFileTimestamps = false
      jar.isReproducibleFileOrder = true
    }

    tasks.named(BasePlugin.ASSEMBLE_TASK_NAME) { it.dependsOn(fileTaskJar) }

    return fileTaskJar
  }

  /**
   * Gets the [LazyPublishArtifact] representation of the jar tasks output.
   */
  private fun jarTaskToPublishArtifact(task: TaskProvider<Jar>): LazyPublishArtifact {
    return LazyPublishArtifact(task, (project as ProjectInternal).fileResolver, (project as ProjectInternal).taskDependencyFactory)
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
    (components.getByName("java") as AdhocComponentWithVariants)
      .addVariantsFromConfiguration(outgoingConfig) { it.mapToOptional() }

    outgoingConfig.outgoing.artifacts.add(jar)
  }

  /**
   * Asserts that fmu dependencies of the project appear as transitive dependencies in the publication.
   * To this end, this method lets the `outgoingfmu2arcSymbols` and `outgoingfmu2arcFiles` configuration of the
   * given [SourceSet] extend from it's `fmu2arcDependencies` configuration.
   * @param sourceSet the [SourceSet] whose symbols should be published and for which this method will
   *        add the transitive dependencies.
   */
  private fun makeIncomingDependenciesToTransitives(sourceSet: SourceSet) {
    val configs = project.configurations
    val dependencyConfig = configs.getByName(sourceSet.fmu2arcDependencyDeclarationConfigName)
    val outgoingSymbolConfig = configs.getByName(sourceSet.fmu2arcOutgoingSymbolsConfigName)
    val outgoingFileConfig = configs.getByName(sourceSet.fmu2arcOutgoingFileConfigName)
    outgoingSymbolConfig.extendsFrom(dependencyConfig)
    outgoingFileConfig.extendsFrom(dependencyConfig)
  }

}

val SourceSet.fmu2arcDependencyDeclarationConfigName: String
  get() = if (SourceSet.isMain(this)) {
    "fmu2arc"
  } else {
    "${this.name}FMU2arc"
  }

val SourceSet.fmu2arcFileDependencyConfigName: String
  get() = if (SourceSet.isMain(this)) {
    "fmu2arcFileDependencies"
  } else {
    "${this.name}FMU2arcFileDependencies"
  }

val SourceSet.fmu2arcOutgoingSymbolsConfigName: String
  get() = if (SourceSet.isMain(this)) {
    "fmu2arcSymbolElements"
  } else {
    "${this.name}fmu2arcSymbolElements"
  }

val SourceSet.fmu2arcOutgoingFileConfigName: String
  get() = if (SourceSet.isMain(this)) {
    "fmu2arcFMUFileElements"
  } else {
    "${this.name}fmu2arcFMUFileElements"
  }

val SourceSet.fmu2arcSymbolsJarTaskName: String
  get() = getTaskName("fmu2arc", "symbolsJar")

val SourceSet.fmu2arcFilesJarTaskName: String
  get() = getTaskName("fmu2arc", "FilesJar")


val SourceSet.fmuSymbolsJarClassifierName: String
  get() = if (SourceSet.isMain(this)) {
    FMU2ARC_SYMBOLS_BASE_CLASSIFIER
  } else {
    "${this.name}-$FMU2ARC_SYMBOLS_BASE_CLASSIFIER"
  }

val SourceSet.fmuFilesJarClassifierName: String
  get() = if (SourceSet.isMain(this)) {
    FMU2ARC_FILES_BASE_CLASSIFIER
  } else {
    "${this.name}-$FMU2ARC_FILES_BASE_CLASSIFIER"
  }
