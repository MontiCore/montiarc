/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import montiarc.gradle.cd2pojo.CD2PojoCompile
import montiarc.gradle.cd2pojo.compileCD2PojoTaskName

/**
 * Connects the outputs of [CD2PojoCompile] to the inputs of [SD2ArcCompile] (for each [SourceSet]).
 */
class CDOut2SDInPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    this.project.pluginManager.apply(SD2ArcPlugin::class.java)

    sourceSetsOf(project).all { sourceSet ->
      connectCDSymbolsToSequenceDiagrams(sourceSet)
      createDependencyBetweenCDAndSDCompileTasks(sourceSet)
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

  private fun connectCDSymbolsToSequenceDiagrams(sourceSet: SourceSet) = with (project) {
    val sdCompile = tasks.named(sourceSet.compileSD2ArcTaskName, SD2ArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java)

    sdCompile.configure {
      it.symbolpath.from(
        provider { cdCompile.get().symbolOutputDir() }
      )
    }
  }

  private fun createDependencyBetweenCDAndSDCompileTasks(sourceSet: SourceSet) = with (project) {
    val sdCompile = tasks.named(sourceSet.compileSD2ArcTaskName, SD2ArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java)

    sdCompile.configure { it.dependsOn(cdCompile) }
  }

  /**
   * Makes symbols of cd source set `main`'s compiled models available in SD2Arc  `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun makeMainModelsAvailableInTests() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainCompile = tasks.named(mainSourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java)
    // Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.compileSD2ArcTaskName, SD2ArcCompile::class.java) {
      it.symbolpath.from(mainCompile.get().symbolOutputDir())
    }
  }
}
