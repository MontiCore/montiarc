/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.fmu2arc.fmu2ArcConfigName
import montiarc.gradle.fmu2arc.fmu2ArcModelpathConfigName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.artifacts.Configuration
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer


/**
 * Creates configurations that allow to declare fmu dependencies of MontiArc models.
 */
@Suppress("unused")
class FMUDependencies4MontiArcPlugin : Plugin<Project>  {
  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with (project) {
      pluginManager.apply(MontiArcDependenciesPlugin::class.java)

      provideResolutionCompatibilityToPlainFMU2Arc()


      sourceSetsOf(project).all { srcSet ->
        addDeclarationConfigTo(srcSet)
        createFMU4MARuntimeConfig(srcSet)
        //need to make sure the plugin is applied so all the configs we need are initialized

        createFMU4MASymbolConfig(srcSet)
        createFMU4MACompSymbolConfig(srcSet)

        connectDependencyConfigsOf(srcSet)

      }

      pluginManager.withPlugin("java") {
        makeMainModelsAvailableInTests()
      }

    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer =
    project.extensions.getByType(JavaPluginExtension::class.java).sourceSets


  /**
   * Allows the resolution of dependency variants with the [Usage] attribute values [FMU2ARC_API_SYMBOL_USAGE] and
   * [FMU2ARC_API_FILE_USAGE], even when we actually search for an [Usage] attribute value of
   * [FMU2ARC_4_MONTIARC_USAGE].
   */
  private fun provideResolutionCompatibilityToPlainFMU2Arc() = with(project) {
    dependencies.attributesSchema.attribute(Usage.USAGE_ATTRIBUTE) {
      it.compatibilityRules.add(FMU2ArcIsValidForMontiArc::class.java)
      it.disambiguationRules.add(FMU2ArcForMontiArcPreferred::class.java)
    }
  }

  /**
   * Creates a configuration used to declare dependencies of montiarc models on fmu2arc models.
   */
  private fun addDeclarationConfigTo(sourceSet: SourceSet) = with(project) {
    val config = configurations.maybeCreate(sourceSet.fmu2arc4montiarcConfigName)
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = true
    config.description = "Used to declare dependencies on fmu2arc models that should be used in MontiArc of source " +
        "set ${sourceSet.name}. This will simultaneously add their java implementation to the implementation " +
        "configuration and their models to the fmu2arc4montiarcSymbolpath configuration."
  }


  /**
   * Creates a configuration (_fmu2arc4montiarcRuntime_) for the given source set, containing fmu models (.fmu).
   * Only use this configuration for processing, but not to declare dependencies! Do the latter
   * using the _fmu2arc4montiarc_ configuration from which _fmu2arc4montiarcRuntime_ extends from, automatically
   * adopting the dependencies.
   */
  private fun createFMU4MARuntimeConfig(sourceSet: SourceSet): Configuration = with(project) {
    val config = configurations.maybeCreate(sourceSet.fmu2arc4montiarcRuntimeConfigName)
    config.isCanBeResolved = true
    config.isCanBeConsumed = false
    config.isVisible = false
    config.description = "Pulls fmu _model_ dependencies that we want to use in our MontiArc models in ${sourceSet.name}"

    addFMU4MAJarAttributesTo(config, project)

    return config
  }

  /**
   * Creates a configuration (_fmu2arc4montiarcSymbolpath_) for the given source set, containing fmu model
   * symbols (.arccsym). Only use this configuration for processing, but not to declare dependencies! Do the latter
   * using the _fmu2arc4montiarc_ configuration from which _fmu2arc4montiarcSymbolpath_ extends from, automatically
   * adopting the dependencies.
   */
  private fun createFMU4MASymbolConfig(sourceSet: SourceSet): Configuration = with(project) {
    val cfg = configurations.maybeCreate(sourceSet.fmu2arc4montiarcSymbolpathConfigName)
    cfg.isCanBeResolved = true
    cfg.isCanBeConsumed = false
    cfg.isVisible = false
    cfg.description = "Contains fmu symbol dependencies for source set ${sourceSet.name}"

    addFMUSymbolAttributesTo(cfg, project)

    return cfg
  }

  /**
   * Creates the resolvable configuration that lets a project import the already-compiled
   * MontiArc component symbols of projects declared via the `fmu2arc` configuration.
   *
   * Without this, `fmu2arc(...)` only pulls in a dependency's raw FMU symbols
   * (see [createFMU4MASymbolConfig]) — it has no way to resolve a `montiarc` component
   * that the dependency itself generated from that FMU. This configuration closes that gap by
   * requesting the same montiarc-symbol variant as [montiarcSymbolpathConfigName],
   * but sourced from the `fmu2arc` declaration config instead of `montiarc`, so consumers don't
   * need a separate, redundant `montiarc(...)` declaration just to import a generated wrapper.
   *
   */
  private fun createFMU4MACompSymbolConfig(sourceSet: SourceSet): Configuration = with(project) {
    val cfg = configurations.maybeCreate(sourceSet.fmu2arc4montiarcCompSymbolpathConfigName)
    cfg.isCanBeResolved = true
    cfg.isCanBeConsumed = false
    cfg.isVisible = false
    cfg.description = "Contains montiarc component symbols (.arcsym) published by fmu2arc-declared " +
        "projects, so consumers of fmu2arc(...) can import generated wrapper components directly."

    addMontiArcSymbolJarAttributesTo(cfg, project)

    return cfg
  }

  /**
   * Configures which configurations, used for declaring MontiArc's fmu dependencies, extend which other configurations.
   *
   * The extensions are as follows:
   * * fmu2arc4montiarcRuntime extends fmu2arc4montiarc,
   * * implementation (from the java plugin) extends fmu2arc4montiarc,
   * * api extends fmu2arc4montiarc (if the java-library plugin is applied),
   * * fmu2arc4montiarc extends montiarc
   * * fmu2arc4montiarc extends fmu2arc (if the fmu2arc plugin is applied)
   * * fmu2arc4montiarcSymbolpath extends fmu2arc (if the fmu2arc plugin is applied)
   * * fmu2arc4montiarcCompSymbolpath extends fmu2arc (if the fmu2arc plugin is applied)
   */
  private fun connectDependencyConfigsOf(sourceSet: SourceSet) = with(project) {
    val fmuDeclConfig = configurations.named(sourceSet.fmu2arc4montiarcConfigName)
    val fmuFileConfig = configurations.named(sourceSet.fmu2arc4montiarcRuntimeConfigName)
    val fmuSymbolConfig = configurations.named(sourceSet.fmu2arc4montiarcSymbolpathConfigName)
    val montiarcConfig = configurations.named(sourceSet.montiarcConfigName)
    val javaImpl = configurations.named(sourceSet.implementationConfigurationName)
    var javaApi: NamedDomainObjectProvider<Configuration>? = null

    // The api dependency configs only exist if java library is applied
    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) { javaApi = configurations.named(sourceSet.apiConfigurationName) }
    }

    // Add links between dependencies
    fmuFileConfig.configure { it.extendsFrom(fmuDeclConfig.get()) }
    javaImpl.configure { it.extendsFrom(fmuDeclConfig.get()) }
    javaApi?.configure { it.extendsFrom(fmuDeclConfig.get()) }
    fmuDeclConfig.configure { it.extendsFrom(montiarcConfig.get()) }

    pluginManager.withPlugin("fmu2arc") {
      // Just like cd2pojo, we dynamically resolve the base fmu2arc configuration
      // if the plugin is applied.
      val fmu2arc = configurations.maybeCreate(sourceSet.fmu2ArcConfigName)
      fmuDeclConfig.configure { fmu -> fmu.extendsFrom(fmu2arc) }

      fmuSymbolConfig.configure{it.extendsFrom(fmu2arc)}

      //make Symbols of MA Components from different Projects accessible
      val fmu2arcMontiArcSymbolConfig = configurations.named(sourceSet.fmu2arc4montiarcCompSymbolpathConfigName)
      fmu2arcMontiArcSymbolConfig.configure{it.extendsFrom (fmu2arc)}
    }
  }

  /**
   * Sets the fmu dependencies of main's MontiArc models to also be the fmu dependencies of test's MontiArc models.
   */
  private fun makeMainModelsAvailableInTests() = with(project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainConfig = configurations.named(mainSourceSet.fmu2arc4montiarcConfigName)
    val testConfig = configurations.named(testSourceSet.fmu2arc4montiarcConfigName)
    testConfig.configure { it.extendsFrom(mainConfig.get()) }


    //make Symbols availabe in Tests and fmu files available at runtime
    pluginManager.withPlugin("fmu2arc") {
      val mainSymbolConfig = configurations.named(mainSourceSet.fmu2arc4montiarcSymbolpathConfigName)
      val testSymbolConfig = configurations.named(testSourceSet.fmu2arc4montiarcSymbolpathConfigName)
      testSymbolConfig.configure { it.extendsFrom(mainSymbolConfig.get()) }

      val mainFmuFileConfig = configurations.named(mainSourceSet.fmu2ArcModelpathConfigName)
      val testRuntimeConfig = configurations.named(testSourceSet.runtimeClasspathConfigurationName)
      testRuntimeConfig.configure { it.extendsFrom(mainFmuFileConfig.get()) }
    }
  }

}
