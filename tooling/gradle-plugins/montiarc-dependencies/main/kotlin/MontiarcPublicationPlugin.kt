/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

const val MONTIARC_SYMBOLS_BASE_CLASSIFIER = "arcSymbols"

/**
 * Adds logic enabling the publication of .arcsym files. However, just applying this plugin does nothing.
 * You have to configure the (montiarc-)symbols-jar task to include the .arcsym files in order to publish
 * a jar with content.
 */
@Suppress("unused")
class MontiarcPublicationPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with (project) {
      pluginManager.apply(MontiarcDependenciesPlugin::class.java)

      pluginManager.withPlugin("java") {
        val mainSourceSet = project.extensions.getByType(JavaPluginExtension::class.java)
          .sourceSets
          .getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        setUpMontiarcSymbolsPublicationFor(mainSourceSet)
      }
    }
  }

  /**
   * Sets up a publication for the symbols of the compiled models of the given source set. To this end, a jar task is
   * created.
   */
  private fun setUpMontiarcSymbolsPublicationFor(sourceSet: SourceSet) {

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

      addMontiarcSymbolJarAttributesTo(config, project)
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
   * Creates a jar task that should package the symbols produced by compiling the models of the source set into a jar.
   * However, the jar task is not configured to contain anything. This is ought to be done by a later plugin.
   */
  private fun createArcSymbolsJarTask(sourceSet: SourceSet): TaskProvider<Jar> = with (project) {
    val arcSymbolsJarTask = tasks.register(sourceSet.montiarcSymbolsJarTaskName, Jar::class.java) { jar ->
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
}

val SourceSet.montiarcSymbolsJarTaskName: String
  get() = getTaskName(null, "montiarcSymbolsJar")

val SourceSet.symbolsJarClassifierName: String
  get() =  if (SourceSet.isMain(this)) {
    MONTIARC_SYMBOLS_BASE_CLASSIFIER
  } else {
    "${this.name}-$MONTIARC_SYMBOLS_BASE_CLASSIFIER"
  }
