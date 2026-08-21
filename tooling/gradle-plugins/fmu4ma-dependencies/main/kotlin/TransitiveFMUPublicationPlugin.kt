/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.fmu2arc.fmu2ArcFilesJarTaskName
import montiarc.gradle.fmu2arc.fmu2ArcSymbolsJarTaskName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.api.artifacts.Configuration

/**
 * Makes that _fmu2arc4montiarc_ dependencies are also declared as dependencies in the publication of
 * the project. This way, other montiarc models will be able to find them and pull them as transitive
 * dependencies, too.
 */
class TransitiveFMUPublicationPlugin : Plugin<Project> {
  private lateinit var project: Project
  override fun apply(project: Project) {
    this.project = project
    with (project) {
      pluginManager.apply(FMUDependencies4MontiArcPlugin::class.java)
      pluginManager.withPlugin("java") {
        val mainSourceSet = extensions.getByType(JavaPluginExtension::class.java)
          .sourceSets
          .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        setUpPublicationOf(mainSourceSet)
      }
    }
  }

  /**
   * Creates an outgoing configuration containing all fmu dependencies of the MontiArc models of the source set and adds
   * it as a variant to the publication of the project. If the fmu2arc plugin is applied, then the fmu-symbols- and
   * fmu-files-jar are also added to the variant.
   */
  private fun setUpPublicationOf(sourceSet: SourceSet) = with(project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to create a publication for source set ${sourceSet.name}, but the " +
          "JavaPlugin is not applied!")
    }
    val outgoingConfig = createOutgoingApiElementsConfig(sourceSet)
    connectOutgoingConfigOf(sourceSet)

    (components.getByName("java") as AdhocComponentWithVariants)  // .mapToOptional results in the jar
      .addVariantsFromConfiguration(outgoingConfig) { it.mapToOptional() }  // being an optional dependency in Maven

    // Copy the Fmu-File jar and generated Symbols jar if fmu2arc is applied
    pluginManager.withPlugin("fmu2arc") {
      val jarTaskSym = tasks.named(sourceSet.fmu2ArcSymbolsJarTaskName, Jar::class.java)
      val symJar = jarTaskToPublishArtifact(jarTaskSym)
      outgoingConfig.outgoing.artifacts.add(symJar)
      val jarTaskFile = tasks.named(sourceSet.fmu2ArcFilesJarTaskName, Jar::class.java)
      val fileJar = jarTaskToPublishArtifact(jarTaskFile)
      outgoingConfig.outgoing.artifacts.add(fileJar)
    }
  }

  /**
   * Creates an outgoing consumable configuration meant to contain all fmu dependencies of the MontiArc models of the
   * source set.
   */
  private fun createOutgoingApiElementsConfig(sourceSet: SourceSet): Configuration = with (project) {
    val config = configurations.maybeCreate(sourceSet.fmu2arc4montiarcApiElementsConfigName)
    config.isCanBeConsumed = true
    config.isCanBeResolved = false
    config.isVisible = false
    config.description = "Publication variant with the fmu dependencies of the MontiArc models in source set " +
        "${sourceSet.name}. Moreover, a copy of the fmu jar is contained in this config."
    addFMU4MAJarAttributesTo(config, project)

    return config
  }

  /**
   * Lets the outgoing config of the source set extend the fmu2arc4montiarc config in order to transfer its dependencies
   */
  private fun connectOutgoingConfigOf(sourceSet: SourceSet) = with (project) {
    val fmu2arcDeclConfig = configurations.named(sourceSet.fmu2arc4montiarcConfigName)
    val outgoingFMU2arcConfig = configurations.named(sourceSet.fmu2arc4montiarcApiElementsConfigName)

    outgoingFMU2arcConfig.configure { it.extendsFrom(fmu2arcDeclConfig.get()) }
  }

  /**
   * Gets the [LazyPublishArtifact] representation of the jar tasks output.
   */
  private fun jarTaskToPublishArtifact(task: TaskProvider<Jar>): LazyPublishArtifact {
    return LazyPublishArtifact(task, (project as ProjectInternal).fileResolver, (project as ProjectInternal).taskDependencyFactory)
  }
}
