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
class MontiarcDependenciesPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with(project) {
      pluginManager.apply("java-base")

      sourceSetsOf(project).all {sourceSet ->
        addDeclarationConfigTo(sourceSet)
        addSymbolDependencyConfigTo(sourceSet)
        letSymbolConfigExtendMontiarcConfigOf(sourceSet)
        letJavaExtendMontiarcConfigOf(sourceSet)
      }

      pluginManager.withPlugin("java") {
        letTestModelsExtendMainModels()
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
    val config = configurations.maybeCreate(sourceSet.montiarcDependencyDeclarationConfigName)
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Used to declare dependencies on other montiarc projects. This will simultaneously add their " +
      "java implementation to the implementation configuration and their models to to the montiarcSymbolDependencies"
  }

  /**
  * Creates a configuration (_montiarcSymbolDependencies_) for the given source set, containing model dependencies
  * (.arcsym etc). Only use this configuration for processing, but not to declare dependencies! Do the latter using the
  * _montiarc_ configuration from which _montiarcSymbolDependencies_ extends from to automatically adopt the
  * dependencies.
  */
  private fun addSymbolDependencyConfigTo(sourceSet: SourceSet) = with (project) {
    configurations.create(sourceSet.montiarcSymbolDependencyConfigurationName) { config ->
      config.isCanBeResolved = true
      config.isCanBeConsumed = false
      config.isVisible = false
      config.description = "Contains montiarc _model_ dependencies (.arcsym, etc). Only use this configuration for " +
        "processing dependencies, but not for declaring them. For declaring them, use the _montiarc_ configuration " +
        "instead."

      addMontiarcSymbolJarAttributesTo(config, project)
    }
  }

  private fun letSymbolConfigExtendMontiarcConfigOf(sourceSet: SourceSet) = with (project) {
    val symbolsConfig = configurations.named(sourceSet.montiarcSymbolDependencyConfigurationName)
    val declarationConfig = configurations.named(sourceSet.montiarcDependencyDeclarationConfigName)

    symbolsConfig.configure { it.extendsFrom(declarationConfig.get()) }
  }

  private fun letJavaExtendMontiarcConfigOf(sourceSet: SourceSet) = with (project) {
    val javaConfig = configurations.named(sourceSet.implementationConfigurationName)
    val montiarcConfig = configurations.named(sourceSet.montiarcDependencyDeclarationConfigName)

    javaConfig.configure { it.extendsFrom(montiarcConfig.get()) }
  }

  /**
   * Lets the `montiarc` configuration of the `test` sourceset extend the `montiarc` configuration of the `main`
   * sourceset so that main's MontiArc dependencies are also available in test's MontiArc models.
   *
   * If this method is called, the `java` plugin must have already been applied. Else, an error is logged.
   */
  private fun letTestModelsExtendMainModels() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = sourceSetsOf(project)
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainModelConfig = configurations.named(mainSourceSet.montiarcDependencyDeclarationConfigName)
    val testModelConfig = configurations.named(testSourceSet.montiarcDependencyDeclarationConfigName)
    testModelConfig.configure { it.extendsFrom(mainModelConfig.get()) }
  }

}
