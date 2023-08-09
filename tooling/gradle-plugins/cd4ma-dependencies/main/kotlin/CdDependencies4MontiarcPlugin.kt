/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.cd2pojo.cd2PojoDependencyDeclarationConfigName
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.*
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Creates configurations that allow to declare class diagram dependencies of MontiArc models. Usage:
 * ```
 * dependencies {
 *   cd2pojo4montiarc("some.other:project.foo:2.0.0")
 *   testCd2pojo4montiarc("Some.other:project.bar:2.0.0")
 * }
 * ```
 */
@Suppress("unused")
class CdDependencies4MontiarcPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with (project) {
      pluginManager.apply(MontiarcDependenciesPlugin::class.java)

      provideResolutionCompatibilityToPlainCd2Pojo()

      sourceSetsOf(project).all { srcSet ->
        addDeclarationConfigTo(srcSet)
        createCd4MaSymbolConfig(srcSet)
        connectDependencyConfigsOf(srcSet)
      }

      pluginManager.withPlugin("java") {
        connectMainToTestConfig()
      }
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer =
    project.extensions.getByType(JavaPluginExtension::class.java).sourceSets

  /**
   * Allows the resolution of dependency variants with the [Usage] attribute value [CD2POJO_API_SYMBOL_USAGE], even when
   * we actually search for an [Usage] attribute value of [CD2POJO_4_MONTIARC_USAGE].
   */

  private fun provideResolutionCompatibilityToPlainCd2Pojo() = with (project) {
    dependencies.attributesSchema.attribute(Usage.USAGE_ATTRIBUTE) {
      it.compatibilityRules.add(Cd2PojoIsValidForMontiArc::class.java)
      it.disambiguationRules.add(Cd2PojoForMontiArcPreferred::class.java)
    }
  }

  /**
   * Creates a configuration used to declare dependencies of montiarc models on cd2pojo models.
   */
  private fun addDeclarationConfigTo(sourceSet: SourceSet) = with (project) {
    val config = configurations.maybeCreate(sourceSet.cd2pojo4MaDeclarationConfigName)
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = true
    config.description = "Used to declare dependencies on cd2pojo models that should be used in MontiArc of source " +
      "set ${sourceSet.name}. This will simultaneously add their java implementation to the implementation " +
      "configuration and their models to to the cd2pojo4MontiarcSymbolDependencies"
  }

  /**
   * Creates a configuration (_cd2pojo4montiarcSymbolDependencies_) for the given source set, containing cd model
   * dependencies (.cdcsym) of the MontiArc models in the same source set. Only use this configuration for processing,
   * but not to declare dependencies! Do the latter using the _cd2pojo4montiarc_ configuration from which
   * _cd2pojo4montiarcSymbolDependencies_ extends from, automatically adopting the dependencies.
   */
  private fun createCd4MaSymbolConfig(sourceSet: SourceSet): Configuration = with (project) {
    val config = configurations.maybeCreate(sourceSet.cd2Pojo4MaSymbolDependencyConfigName)
    config.isCanBeResolved = true
    config.isCanBeConsumed = false
    config.isVisible = false
    config.description = "Pulls cd _model_ dependencies that we want to use in our MontiArc models in ${sourceSet.name}"
    addCd4maJarAttributesTo(config, project)

    return config
  }

  /**
   * Configures which configurations, used for declaring MontiArc's cd dependencies, extend which other configurations.
   * Moreover, java's configurations are also wired together with cd2pojo4montiarc in order to load class diagram
   * implementations.
   *
   * The extensions are as follows:
   * * cd2pojo4montiarcSymbolDependencies extends cd2pojo4montiarc,
   * * implementation (from the java plugin) extends cd2pojo4montiarc,
   * * api extends cd2pojo4montiarc (if the java-library plugin is applied),
   * * cd2pojo4montiarc extends montiarc
   * * cd2pojo4montiarc extends cd2pojo (if the cd2pojo plugin is applied)
   */
  private fun connectDependencyConfigsOf(sourceSet: SourceSet) = with(project) {
    val cd4maDeclConfig = configurations.named(sourceSet.cd2pojo4MaDeclarationConfigName)
    val cd4maSymbolConfig = configurations.named(sourceSet.cd2Pojo4MaSymbolDependencyConfigName)
    val montiarcConfig = configurations.named(sourceSet.montiarcDependencyDeclarationConfigName)
    val javaImpl = configurations.named(sourceSet.implementationConfigurationName)
    var javaApi: NamedDomainObjectProvider<Configuration>? = null

    // The api dependency configs only exist if java library is applied
    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) { javaApi = configurations.named(sourceSet.apiConfigurationName) }
    }

    // Add links between dependencies
    cd4maSymbolConfig.configure { it.extendsFrom(cd4maDeclConfig.get()) }
    javaImpl.configure { it.extendsFrom(cd4maDeclConfig.get()) }
    javaApi?.configure { it.extendsFrom(cd4maDeclConfig.get()) }
    cd4maDeclConfig.configure { it.extendsFrom(montiarcConfig.get()) }
    pluginManager.withPlugin("cd2pojo") {
      // We want that cd2pojo4ma extends from cd2pojo to inherit its dependencies. However, Gradle does not allow to
      // extend from Provider<Configuration>, so we have to actually resolve that config. The problem is that the
      // cd2pojo config is created by a closure from another plugin and we do not have control of whether that closure
      // is called before this piece of code. The solution is to call `maybeCreate`, creating the config only if it
      // was not present yet. We do not configure it here, as this will be done by the closure declared in the cd2pojo
      // plugin.
      val cd2pojo = configurations.maybeCreate(sourceSet.cd2PojoDependencyDeclarationConfigName)
      cd4maDeclConfig.configure { cd4ma -> cd4ma.extendsFrom(cd2pojo) }
    }
  }

  /**
   * Sets the cd dependencies of main's MontiArc models to also be the cd dependencies of test's MontiArc models.
   */
  private fun connectMainToTestConfig() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainConfig = configurations.named(mainSourceSet.cd2pojo4MaDeclarationConfigName)
    val testConfig = configurations.named(testSourceSet.cd2pojo4MaDeclarationConfigName)
    testConfig.configure { it.extendsFrom(mainConfig.get())}
  }

}


