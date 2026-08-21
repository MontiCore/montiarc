/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Creates configurations that allow depending on MontiArc models. Usage:
 * ```
 * dependencies {
 *   montiarc("some.other:project.foo:2.0.0")
 *   testMontiarc("Some.other:project.bar:2.0.0")
 * }
 * ```
 */
@Suppress("unused")
class MontiArcDependenciesPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with(project) {
      pluginManager.apply("java-base")

      sourceSetsOf(project).all {sourceSet ->
        addDeclarationConfigTo(sourceSet)
        createMontiArcSymbolpathConfig(sourceSet)
        connectDependencyConfigsOf(sourceSet)
      }

      pluginManager.withPlugin("java") {
        makeMainModelsAvailableInTests()
      }
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer =
    project.extensions.getByType(JavaPluginExtension::class.java).sourceSets

  /**
   * Creates a configuration used to declare dependencies on montiarc models and their implementation simultaneously.
   * To this end, the _implementation_ configuration of the source set extends from the created _montiarc_ configuration
   */
  private fun addDeclarationConfigTo(sourceSet: SourceSet) = with (project) {
    val config = configurations.maybeCreate(sourceSet.montiarcConfigName)
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Used to declare dependencies on other montiarc projects. This will simultaneously add their " +
      "java implementation to the implementation configuration and their models to to the montiarcSymbolpath configuration"
  }

  /**
  * Creates a configuration (_montiarcSymbolpath_) for the given source set, containing model dependencies
  * (.arcsym etc). Only use this configuration for processing, but not to declare dependencies! Do the latter using the
  * _montiarc_ configuration from which _montiarcSymbolpath_ extends from to automatically adopt the
  * dependencies.
  */
  private fun createMontiArcSymbolpathConfig(sourceSet: SourceSet) = with (project) {
    configurations.create(sourceSet.montiarcSymbolpathConfigName) { config ->
      config.isCanBeResolved = true
      config.isCanBeConsumed = false
      config.isVisible = false
      config.description = "Contains montiarc _model_ dependencies (.arcsym, etc). Only use this configuration for " +
        "processing dependencies, but not for declaring them. For declaring them, use the _montiarc_ configuration " +
        "instead."

      addMontiArcSymbolJarAttributesTo(config, project)
    }
  }

  private fun connectDependencyConfigsOf(sourceSet: SourceSet) = with (project) {
    val symbolsConfig = configurations.named(sourceSet.montiarcSymbolpathConfigName)
    val declarationConfig = configurations.named(sourceSet.montiarcConfigName)
    val javaConfig = configurations.named(sourceSet.implementationConfigurationName)

    symbolsConfig.configure { it.extendsFrom(declarationConfig.get()) }
    javaConfig.configure { it.extendsFrom(declarationConfig.get()) }
  }

  /**
   * Lets the `montiarc` configuration of the `test` sourceset extend the `montiarc` configuration of the `main`
   * sourceset so that main's MontiArc dependencies are also available in test's MontiArc models.
   *
   * If this method is called, the `java` plugin must have already been applied. Else, an error is logged.
   */
  private fun makeMainModelsAvailableInTests() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = sourceSetsOf(project)
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainModelConfig = configurations.named(mainSourceSet.montiarcConfigName)
    val testModelConfig = configurations.named(testSourceSet.montiarcConfigName)
    testModelConfig.configure { it.extendsFrom(mainModelConfig.get()) }
  }

}
