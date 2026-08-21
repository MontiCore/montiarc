/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import montiarc.gradle.cd2pojo.CD2PojoCompile
import montiarc.gradle.cd2pojo.compileCD2PojoTaskName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Connects the outputs of [CD2PojoCompile] to the inputs of [MontiArcCompile] (for each [SourceSet]).
 */
class CDOut2MAInPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    this.project.pluginManager.apply(MA2JSimPlugin::class.java)

    sourceSetsOf(project).all { sourceSet ->
      connectCDSymbolsToMontiArc(sourceSet)
      createDependencyBetweenCDAndMACompileTasks(sourceSet)
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

  private fun connectCDSymbolsToMontiArc(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java)

    maCompile.configure {
      it.symbolpath.from(
        provider { cdCompile.get().symbolOutputDir() }
      )
    }
  }

  private fun createDependencyBetweenCDAndMACompileTasks(sourceSet: SourceSet) = with (project) {
    val maCompile = tasks.named(sourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java)

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

    val mainCDCompile = tasks.named(mainSourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java)
    val testMACompile = tasks.named(testSourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)

    // Puts main's symbols on the symbol path of test
    testMACompile.configure {
      it.symbolpath.from(
          provider { mainCDCompile.get().symbolOutputDir() }
      )
    }
  }
}
