/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import montiarc.gradle.cd2pojo.Cd2PojoCompile
import montiarc.gradle.cd2pojo.compileCd2PojoTaskName

/**
 * Connects the outputs of [Cd2PojoCompile] to the inputs of [Sd2ArcCompile] (for each [SourceSet]).
 */
class CDOut2SDInPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project
    this.project.pluginManager.apply(Sd2ArcPlugin::class.java)

    sourceSetsOf(project).all { sourceSet ->
      connectCdSymbolsToSequenceDiagrams(sourceSet)
      createDependencyBetweenCdAndSdCompileTasks(sourceSet)
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

  private fun connectCdSymbolsToSequenceDiagrams(sourceSet: SourceSet) = with (project) {
    val sdCompile = tasks.named(sourceSet.compileSd2ArcTaskName, Sd2ArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCd2PojoTaskName, Cd2PojoCompile::class.java)

    sdCompile.configure {
      it.symbolImportDir.from(
        provider { cdCompile.get().symbolOutputDir() }
      )
    }
  }

  private fun createDependencyBetweenCdAndSdCompileTasks(sourceSet: SourceSet) = with (project) {
    val sdCompile = tasks.named(sourceSet.compileSd2ArcTaskName, Sd2ArcCompile::class.java)
    val cdCompile = tasks.named(sourceSet.compileCd2PojoTaskName, Cd2PojoCompile::class.java)

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

    val mainCompile = tasks.named(mainSourceSet.compileCd2PojoTaskName, Cd2PojoCompile::class.java)
    // Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.compileSd2ArcTaskName, Sd2ArcCompile::class.java) {
      it.symbolImportDir.from(mainCompile.get().symbolOutputDir())
    }
  }
}