/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import montiarc.gradle.cd2pojo.Cd2PojoCompile
import montiarc.gradle.cd2pojo.compileCd2PojoTaskName

/**
 * Connects the outputs of [Cd2PojoCompile] to the inputs of [MontiArcCompile] (for each [SourceSet]).
 */
class CDOut2MAInPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    this.project.pluginManager.apply(Ma2JavaPlugin::class.java)

    sourceSetsOf(project).all { sourceSet ->
      connectCdSymbolsToMontiarc(sourceSet)
      createDependencyBetweenCdAndMaCompileTasks(sourceSet)
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer = with (project) {
    return extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  private fun connectCdSymbolsToMontiarc(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.compileMontiarcTaskName, MontiArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCd2PojoTaskName, Cd2PojoCompile::class.java)

    maCompile.configure {
      it.symbolImportDir.from(
        provider { cdCompile.get().symbolOutputDir() }
      )
    }
  }

  private fun createDependencyBetweenCdAndMaCompileTasks(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.compileMontiarcTaskName, MontiArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCd2PojoTaskName, Cd2PojoCompile::class.java)

    maCompile.configure { it.dependsOn(cdCompile) }
  }

}