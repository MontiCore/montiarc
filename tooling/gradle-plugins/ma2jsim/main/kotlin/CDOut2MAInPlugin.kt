/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import montiarc.gradle.cd2pojo.Cd2PojoCompile
import montiarc.gradle.cd2pojo.compileCd2PojoTaskName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

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

    with (project) {
      pluginManager.withPlugin("java") {
        makeMainModelsAvailableInTests()
      }
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
      it.setIgnoreExitValue(true)
    }
  }

  private fun createDependencyBetweenCdAndMaCompileTasks(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.compileMontiarcTaskName, MontiArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCd2PojoTaskName, Cd2PojoCompile::class.java)

    maCompile.configure { it.dependsOn(cdCompile) }
  }

  /**
   * Makes symbols of cd source set `main`'s compiled models available in MontiArc  `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun makeMainModelsAvailableInTests() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainCompile = tasks.named(mainSourceSet.compileCd2PojoTaskName, Cd2PojoCompile::class.java)
    // Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.compileMontiarcTaskName, MontiArcCompile::class.java) {
      it.symbolImportDir.from(mainCompile.get().symbolOutputDir())
    }
  }
}