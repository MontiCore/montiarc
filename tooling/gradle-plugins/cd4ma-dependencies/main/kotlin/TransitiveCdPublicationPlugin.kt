/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.cd2pojo.cd2PojoSymbolsJarTaskName
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

/**
 * Makes that _cd2pojo4montiarc_ dependencies are also declared as dependencies in the publication of
 * the project. This way, other montiarc models will be able to find them and pull them as transitive
 * dependencies, too.
 */
class TransitiveCdPublicationPlugin : Plugin<Project> {

  private lateinit var project: Project
  override fun apply(project: Project) {
    this.project = project

    with (project) {
      pluginManager.apply(CdDependencies4MontiarcPlugin::class.java)

      pluginManager.withPlugin("java") {
        val mainSourceSet = extensions.getByType(JavaPluginExtension::class.java)
          .sourceSets
          .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        setUpPublicationOf(mainSourceSet)
      }
    }
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
    addCd4maJarAttributesTo(config, project)

    return config
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
