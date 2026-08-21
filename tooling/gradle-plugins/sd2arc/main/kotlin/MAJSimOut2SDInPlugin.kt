/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import montiarc.gradle.ma2jsim.MontiArcCompile
import montiarc.gradle.ma2jsim.compileMontiArcTaskName

/**
 * Connects the outputs of [MontiArcCompile] to the inputs of [SD2ArcCompile] (for each [SourceSet]).
 */
class MAJSimOut2SDInPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    this.project.pluginManager.apply(SD2ArcPlugin::class.java)

    with (project) {
      pluginManager.withPlugin("java") {
        makeMainModelsAvailableInTests()
      }
    }
  }

  /**
   * Makes symbols of MA source set `main`'s compiled models available in SD2Arc  `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun makeMainModelsAvailableInTests() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainCompile = tasks.named(mainSourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)
    // Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.compileSD2ArcTaskName, SD2ArcCompile::class.java) {
      it.symbolpath.from(mainCompile.get().symbolOutputDir())
    }
  }
}
