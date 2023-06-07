/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import montiarc.tooling.cd2pojo.plugin.CD2POJO_API_SYMBOL_USAGE
import montiarc.tooling.cd2pojo.plugin.cd2PojoDependencyDeclarationConfigName
import montiarc.tooling.cd2pojo.plugin.cd2PojoSymbolsJarTaskName
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.*
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

const val CD2POJO_4_MONTIARC_USAGE = "cd2pojo-for-montiarc-api"

/**
 * Allows to declare cd2pojo model dependencies from MontiArc models. To this end,
 * two to three configurations are added to each source set and wired up afterwards.
 */
class Cd2PojoDependencies4MAPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    this.project.pluginManager.apply(MontiarcPlugin::class.java)

    with (project) {
      provideResolutionCompatibilityToPlainCd2Pojo()

      extensions.getByType(JavaPluginExtension::class.java)
        .sourceSets.all { srcSet ->
          createCd4MaDeclarationConfig(srcSet)
          createCd4MaSymbolConfig(srcSet)
          connectDependencyConfigsOf(srcSet)
          addCdModelsToMontiArcCompileOf(srcSet)
        }

      pluginManager.withPlugin("java") {
        connectMainToTestConfig()

        val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
        val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        setUpPublicationOf(mainSourceSet)
      }

    }
  }

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
  private fun createCd4MaDeclarationConfig(sourceSet: SourceSet): Configuration = with (project) {
    val config = configurations.maybeCreate(sourceSet.cd2pojo4MaDeclarationConfigName)
    config.isCanBeConsumed = false
    config.isCanBeResolved = false
    config.isVisible = true
    config.description = "Used to declare dependencies on cd2pojo models that should be used in MontiArc of source " +
      "set ${sourceSet.name}. This will simultaneously add their java implementation to the implementation " +
      "configuration and their models to to the cd2pojo4MontiarcSymbolDependencies"

    return config
  }

  /**
   * The publication metadata for the set of transitive cd dependencies that the MontiArc models have.
   * (Most notably, the [Usage] attribute value is [CD2POJO_4_MONTIARC_USAGE]
   */
  private val cd4maJarAttributes = Action<AttributeContainer> {
    it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
    it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, CD2POJO_4_MONTIARC_USAGE))
    it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
    it.attribute(
      LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
    )
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
    config.attributes(cd4maJarAttributes)

    return config
  }

  /**
   * Configures which configurations, used for declaring MontiArc's cd dependencies, extend which other configurations.
   * Moreover, java's configurations are also wired together with cd2pojo4montiarc in order to load class diagram
   * implementations.
   *
   * The extensions are as follows:
   * cd2pojo4montiarcSymbolDependencies extends cd2pojo4montiarc,
   * implementation (from the java plugin) extends cd2pojo4montiarc,
   * api extends cd2pojo4montiarc (if the java-library plugin is applied),
   * cd2pojo4montiarc extends montiarc,
   * cd2pojo4montiarc extends cd2pojo (if the cd2pojo plugin is applied)
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
   * Puts all cd dependencies of the MontiArc models in the sourceSet on the [MontiArcCompile.symbolImportDir] of the
   * sourceSet.
   */
  private fun addCdModelsToMontiArcCompileOf(sourceSet: SourceSet) = with (project) {
    val cd4maSyms = configurations.named(sourceSet.cd2Pojo4MaSymbolDependencyConfigName)

    tasks.named(sourceSet.compileMontiarcTaskName, MontiarcCompile::class.java) {
      it.symbolImportDir.from(cd4maSyms)
    }
  }

  /**
   * Sets the cd dependencies of main's MontiArc models to also be the cd dependencies of test's MontiArc models.
   */
  private fun connectMainToTestConfig() = with (project) {
    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainConfig = configurations.named(mainSourceSet.cd2pojo4MaDeclarationConfigName)
    val testConfig = configurations.named(testSourceSet.cd2pojo4MaDeclarationConfigName)
    testConfig.configure { it.extendsFrom(mainConfig.get())}
  }

  /**
   * Creates an outgoing configuration containing all cd dependencies of the MontiArc models of the source set and adds
   * it as a variant to the publication of the project. If the cd2pojo plugin is applied, then the cd-symbols-jar is
   * also added to the variant.
   */
  private fun setUpPublicationOf(sourceSet: SourceSet) = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to create a publication for source set ${sourceSet.name}, but the " +
        "JavaPlugin is not applied!")
    }

    val outgoingConfig = createCd4MaOutgoingConfigFor(sourceSet)
    connectOutgoingConfigOf(sourceSet)

    (components.getByName("java") as AdhocComponentWithVariants)  // .mapToOptional results in the jar
      .addVariantsFromConfiguration(outgoingConfig) { it.mapToOptional() }  // being an optional dependency in Maven

    // Copy the cd2pojo symbols jar if cd2pojo is applied
    pluginManager.withPlugin("cd2pojo") {
      val jarTask = tasks.named(sourceSet.cd2PojoSymbolsJarTaskName, Jar::class.java)
      val jar = jarTaskToPublishArtifact(jarTask)
      outgoingConfig.outgoing.artifacts.add(jar)
    }
  }

  /**
   * Creates an outgoing consumable configuration meant to contain all cd dependencies of the MontiArc models of the
   * source set.
   */
  private fun createCd4MaOutgoingConfigFor(sourceSet: SourceSet): Configuration = with (project) {
    val config = configurations.maybeCreate(sourceSet.outgoingCd4MaDependenciesConfigName)
    config.isCanBeConsumed = true
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Publication variant with the cd dependencies of the MontiArc models in source set " +
      "${sourceSet.name}. Moreover, a copy of the cd symbols jar is contained in this config."
    config.attributes(cd4maJarAttributes)
  }

  /**
   * Lets the outgoing config of the source set extend the cd2pojo4montiarc config in order to transfer its dependencies
   */
  private fun connectOutgoingConfigOf(sourceSet: SourceSet) = with (project) {
    val cd4maDeclConfig = configurations.named(sourceSet.cd2pojo4MaDeclarationConfigName)
    val outgoingCd4maConfig = configurations.named(sourceSet.outgoingCd4MaDependenciesConfigName)

    outgoingCd4maConfig.configure { it.extendsFrom(cd4maDeclConfig.get()) }
  }

  /**
   * Gets the [LazyPublishArtifact] representation of the jar tasks output.
   */
  private fun jarTaskToPublishArtifact(task: TaskProvider<Jar>): LazyPublishArtifact {
    return LazyPublishArtifact(task, project.version.toString(), (project as ProjectInternal).fileResolver)
  }
}

/**
 * Using this rule, dependency variants with the [Usage] attribute value [CD2POJO_API_SYMBOL_USAGE] are compatible when
 * we actually search for an [Usage] attribute value of [CD2POJO_4_MONTIARC_USAGE].
 */
class Cd2PojoIsValidForMontiArc : AttributeCompatibilityRule<Usage> {
  override fun execute(t: CompatibilityCheckDetails<Usage>): Unit = with (t) {
    if (consumerValue != null && consumerValue!!.name == CD2POJO_4_MONTIARC_USAGE
      && producerValue != null && (
        producerValue!!.name == CD2POJO_4_MONTIARC_USAGE || producerValue!!.name == CD2POJO_API_SYMBOL_USAGE)
      ) {
      compatible()
    }
  }
}

/**
 * Using this rule, dependency variants with perfectly matching [Usage] attribute value [CD2POJO_4_MONTIARC_USAGE] are
 * preferred over [CD2POJO_API_SYMBOL_USAGE] when we search for [Usage] values of [CD2POJO_4_MONTIARC_USAGE].
 */
class Cd2PojoForMontiArcPreferred : AttributeDisambiguationRule<Usage> {
  override fun execute(t: MultipleCandidatesDetails<Usage>): Unit = with (t) {
    if (consumerValue != null && consumerValue!!.name == CD2POJO_4_MONTIARC_USAGE) {

      val candidates = candidateValues.filterNotNull()
      val equalMatch = candidates.firstOrNull { it.name == CD2POJO_4_MONTIARC_USAGE }
      val fromOrigLang = candidates.firstOrNull { it.name == CD2POJO_API_SYMBOL_USAGE }

      if (equalMatch != null) {
        closestMatch(equalMatch)
      } else if (fromOrigLang != null) {
        closestMatch(fromOrigLang)
      }
    }
  }
}

val SourceSet.cd2pojo4MaDeclarationConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "cd2pojo4montiarc"
    } else {
      "${this.name}Cd2pojo4montiarc"
    }

val SourceSet.cd2Pojo4MaSymbolDependencyConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "cd2pojo4montiarcSymbolDependencies"
    } else {
      "${this.name}Cd2pojo4montiarcSymbolDependencies"
    }

val SourceSet.outgoingCd4MaDependenciesConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "cd2pojo4montiarcSymbolDependencyElements"
    } else {
      "${this.name}Cd2pojo4montiarcSymbolDependencyElements"
    }