/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import montiarc.tooling.cd2pojo.plugin.Cd2PojoCompile
import montiarc.tooling.cd2pojo.plugin.getCompileCd2PojoTaskName

/**
 * Connects the outputs of [Cd2PojoCompile] to the inputs of [MontiarcCompile] (for each [SourceSet]).
 */
class ConnectCd2PojoToMACompilePlugin : Plugin<Project> {

  private lateinit var project: Project
  override fun apply(project: Project) {
    this.project = project

    sourceSetsOf(project).all { sourceSet ->
      connectCdSymbolsToMontiarc(sourceSet)
      createDependencyBetweenCdAndMaCompileTasks(sourceSet)
      // TODO: add cd dependency config for montiarc
      //       also add transitive dependencies of the cd models to the dependencies of montiarc
      //       maybe this could be performed within the montiarc plugin
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer = with (project) {
    return extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  private fun connectCdSymbolsToMontiarc(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.getCompileMontiarcTaskName(), MontiarcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.getCompileCd2PojoTaskName(), Cd2PojoCompile::class.java)

    maCompile.configure {
      it.symbolImportDir.from(
        provider { cdCompile.get().symbolOutputDir() }
      )
    }
  }

  private fun createDependencyBetweenCdAndMaCompileTasks(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.getCompileMontiarcTaskName(), MontiarcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.getCompileCd2PojoTaskName(), Cd2PojoCompile::class.java)

    maCompile.configure { it.dependsOn(cdCompile) }
  }

}