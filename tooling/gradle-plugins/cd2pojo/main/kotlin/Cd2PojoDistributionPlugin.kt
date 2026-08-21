/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

/**
 * Configures dependency resolution and variant publication for CD2Pojo symbol artifacts.
 *
 * For every source set, this plugin configures:
 * - a declarable configuration for dependencies on CD2Pojo model projects;
 * - a resolvable symbol-path configuration that supplies `.cdsym` artifacts to CD2Pojo;
 * - an optional, consumable API-elements variant containing generated symbols.
 *
 * For Java projects, only the `main` symbols are published as a variant of the
 * `java` software component.
 */
class CD2PojoDistributionPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    project.pluginManager.apply(CD2PojoPlugin::class.java)

    with(project) {
      extensions.getByType(JavaPluginExtension::class.java)
        .sourceSets
        .all { sourceSet ->
          val cd2PojoConfiguration = addDeclarationConfigTo(sourceSet)

          val cd2PojoSymbolpathConfiguration =
            createCD2PojoSymbolpathConfig(
              sourceSet,
              cd2PojoConfiguration
            )

          configureCD2PojoSymbolpath(
            sourceSet,
            cd2PojoSymbolpathConfiguration
          )
        }

      /*
       * `main` and `test` are provided by the Java plugin. The callback is invoked
       * immediately when the Java plugin has already been applied; otherwise, it is
       * invoked when the plugin is applied.
       */
      pluginManager.withPlugin("java") {
        makeMainModelsAvailableInTests()
        configureMainCD2PojoSymbolPublication()
      }
    }
  }

  /**
   * Creates the declarable configuration for dependencies on CD2Pojo model projects.
   *
   * The source set's `implementation` configuration extends from this configuration.
   * If the Java Library plugin is applied, the `main` source set's `api` configuration
   * also extends from it.
   */
  private fun addDeclarationConfigTo(
    sourceSet: SourceSet
  ): Configuration = with(project) {
    val cd2PojoConfiguration = configurations.maybeCreate(
      sourceSet.cd2pojoConfigName
    )

    cd2PojoConfiguration.isCanBeConsumed = false
    cd2PojoConfiguration.isCanBeResolved = false
    cd2PojoConfiguration.isVisible = false
    cd2PojoConfiguration.description =
      "Declares dependencies on CD2Pojo model projects. Declared dependencies are " +
          "available to the implementation configuration and CD2Pojo symbol path."

    configurations.named(sourceSet.implementationConfigurationName) {
      it.extendsFrom(cd2PojoConfiguration)
    }

    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) {
        configurations.named(sourceSet.apiConfigurationName) {
          it.extendsFrom(cd2PojoConfiguration)
        }
      }
    }

    cd2PojoConfiguration
  }

  /**
   * Creates the resolvable configuration supplying CD2Pojo symbol artifacts required
   * on the symbol path of [sourceSet].
   *
   * Declare dependencies through [SourceSet.cd2pojoConfigName]. This configuration
   * inherits those dependencies and resolves their `.cdsym` artifacts.
   */
  private fun createCD2PojoSymbolpathConfig(
    sourceSet: SourceSet,
    cd2PojoConfiguration: Configuration
  ): Configuration {
    return project.configurations.create(sourceSet.cd2pojoSymbolpathConfigName) {
      it.extendsFrom(cd2PojoConfiguration)
      it.isCanBeResolved = true
      it.isCanBeConsumed = false
      it.isVisible = false
      it.description =
        "Resolves CD2Pojo symbol artifacts, such as .cdsym files, required on the " +
            "CD2Pojo symbol path. Declare dependencies using the " +
            "${sourceSet.cd2pojoConfigName} configuration."

      attachCD2PojoSymbolAttributes(it.attributes, project, LibraryElements.JAR)
    }
  }

  /**
   * Adds the resolved artifacts from [cd2PojoSymbolpathConfiguration] to the
   * [CD2PojoCompile.symbolpath] of [sourceSet]'s CD2Pojo compile task.
   */
  private fun configureCD2PojoSymbolpath(
    sourceSet: SourceSet,
    cd2PojoSymbolpathConfiguration: Configuration
  ) = with(project) {
    tasks.named(sourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java) {
      it.symbolpath.from(cd2PojoSymbolpathConfiguration)
    }
  }

  /**
   * Makes symbols compiled from `main` models available while compiling `test` models.
   *
   * The `testCd2pojo` configuration extends from `cd2pojo`. The symbols output by
   * the `main` CD2Pojo compile task is also added to the `test` task's symbol path.
   */
  private fun makeMainModelsAvailableInTests() = with(project) {
    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainCD2PojoConfiguration = configurations.getByName(
      mainSourceSet.cd2pojoConfigName
    )
    val testCD2PojoConfiguration = configurations.getByName(
      testSourceSet.cd2pojoConfigName
    )
    testCD2PojoConfiguration.extendsFrom(mainCD2PojoConfiguration)

    val mainCD2PojoCompileTask = tasks.named(
      mainSourceSet.compileCD2PojoTaskName,
      CD2PojoCompile::class.java
    )

    tasks.named(
      testSourceSet.compileCD2PojoTaskName,
      CD2PojoCompile::class.java
    ) {
      it.symbolpath.from(
        mainCD2PojoCompileTask.flatMap(CD2PojoCompile::symbolOutputDir)
      )
    }
  }

  /**
   * Configures publication of compiled CD2Pojo symbols from the `main` source set.
   */
  private fun configureMainCD2PojoSymbolPublication() = with(project) {
    val mainSourceSet = extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
      .getByName(SourceSet.MAIN_SOURCE_SET_NAME)

    configureCD2PojoSymbolPublication(mainSourceSet)
  }

  /**
   * Configures the consumable CD2Pojo API-elements variant for [sourceSet].
   *
   * The generated symbols are packaged into a JAR and exposed as an optional variant
   * of the `java` software component.
   */
  private fun configureCD2PojoSymbolPublication(sourceSet: SourceSet) {
    val cd2PojoApiElementsConfiguration =
      createOutgoingApiElementsConfig(sourceSet)

    val cd2PojoSymbolsJarTask = createCD2PojoSymbolsJarTask(sourceSet)

    setUpPublicationOf(
      cd2PojoSymbolsJarTask,
      cd2PojoApiElementsConfiguration
    )
    connectOutgoingConfigOf(sourceSet)
  }

  /**
   * Creates the consumable API-elements configuration exposing compiled CD2Pojo
   * symbols for [sourceSet].
   */
  private fun createOutgoingApiElementsConfig(
    sourceSet: SourceSet
  ): Configuration {
    return project.configurations.create(
      sourceSet.cd2pojoApiElementsConfigName
    ) { cd2PojoApiElementsConfiguration ->
      cd2PojoApiElementsConfiguration.isCanBeConsumed = true
      cd2PojoApiElementsConfiguration.isCanBeResolved = false
      cd2PojoApiElementsConfiguration.description =
        "Contains symbols compiled from CD2Pojo models in source set '${sourceSet.name}'."

      attachCD2PojoSymbolAttributes(
        cd2PojoApiElementsConfiguration.attributes,
        project,
        LibraryElements.JAR
      )
    }
  }

  /**
   * Creates the task that packages symbols compiled from [sourceSet]'s CD2Pojo
   * models into a JAR.
   */
  private fun createCD2PojoSymbolsJarTask(
    sourceSet: SourceSet
  ): TaskProvider<Jar> = with(project) {
    val cd2PojoCompileTask = tasks.named(
      sourceSet.compileCD2PojoTaskName,
      CD2PojoCompile::class.java
    )

    val cd2PojoSymbolsJarTask = tasks.register(
      sourceSet.cd2pojoSymbolsJarTaskName,
      Jar::class.java
    ) {
      it.from(cd2PojoCompileTask.flatMap(CD2PojoCompile::symbolOutputDir))
      it.archiveClassifier.set(sourceSet.cd2pojoSymbolsJarClassifierName)
      it.isPreserveFileTimestamps = false
      it.isReproducibleFileOrder = true
    }

    tasks.named(BasePlugin.ASSEMBLE_TASK_NAME) {
      it.dependsOn(cd2PojoSymbolsJarTask)
    }

    cd2PojoSymbolsJarTask
  }

  /**
   * Adds [cd2PojoSymbolsJarTask] as an outgoing artifact of
   * [cd2PojoApiElementsConfiguration] and exposes the configuration as an optional
   * variant of the `java` software component.
   */
  private fun setUpPublicationOf(
    cd2PojoSymbolsJarTask: TaskProvider<Jar>,
    cd2PojoApiElementsConfiguration: Configuration
  ) = with(project) {
    cd2PojoApiElementsConfiguration.outgoing.artifact(cd2PojoSymbolsJarTask)

    (components.getByName("java") as AdhocComponentWithVariants)
      .addVariantsFromConfiguration(cd2PojoApiElementsConfiguration) {
        it.mapToOptional()
      }
  }

  /**
   * Makes dependencies declared through [SourceSet.cd2pojoConfigName] transitive
   * dependencies of [sourceSet]'s published CD2Pojo API-elements variant.
   */
  private fun connectOutgoingConfigOf(sourceSet: SourceSet) {
    val cd2PojoConfiguration = project.configurations.getByName(
      sourceSet.cd2pojoConfigName
    )
    val cd2PojoApiElementsConfiguration = project.configurations.getByName(
      sourceSet.cd2pojoApiElementsConfigName
    )

    cd2PojoApiElementsConfiguration.extendsFrom(cd2PojoConfiguration)
  }
}
