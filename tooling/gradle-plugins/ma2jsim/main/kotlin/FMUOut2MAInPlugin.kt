/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import montiarc.gradle.fmu2arc.FMU2ArcCompile
import montiarc.gradle.fmu2arc.compileFMU2ArcTaskName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Connects the outputs of [FMU2ArcCompile] to the inputs of [MontiArcCompile] (for each [SourceSet]).
 */
class FMUOut2MAInPlugin : Plugin<Project>  {
  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    this.project.pluginManager.apply(MA2JSimPlugin::class.java)

    sourceSetsOf(project).all { sourceSet ->
      connectFMUSymbolsToMontiArc(sourceSet)
      createDependencyBetweenFMUAndMACompileTasks(sourceSet)
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
  private fun connectFMUSymbolsToMontiArc(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)
    val fmuCompile = tasks.named(sourceSet.compileFMU2ArcTaskName, FMU2ArcCompile::class.java)

    maCompile.configure {
      it.symbolpath.from(
        provider { fmuCompile.get().symbolOutputDir() }
      )
    }
  }
  private fun createDependencyBetweenFMUAndMACompileTasks(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)
    val fmuCompile = tasks.named(sourceSet.compileFMU2ArcTaskName, FMU2ArcCompile::class.java)

    maCompile.configure {
      it.dependsOn(fmuCompile)
    }
  }

  /**
   * Makes symbols of fmu source set `main`'s compiled models available in MontiArc  `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun makeMainModelsAvailableInTests() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainFMUCompile = tasks.named(mainSourceSet.compileFMU2ArcTaskName, FMU2ArcCompile::class.java)
    val testMACompile = tasks.named(testSourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)

    // Puts main's symbols on the symbol path of test
    testMACompile.configure {
      it.symbolpath.from(
        provider { mainFMUCompile.get().symbolOutputDir() }
      )
    }
  }
}

