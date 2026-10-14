/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.arc2fd

import montiarc.gradle.montiarc.MontiArcBasePlugin
import montiarc.gradle.montiarc.cd2pojo4montiarcSymbolpathConfigName
import montiarc.gradle.montiarc.fmu2arc4montiarcCompSymbolpathConfigName
import montiarc.gradle.montiarc.fmu2arc4montiarcSymbolpathConfigName
import montiarc.gradle.montiarc.montiarc
import montiarc.gradle.montiarc.montiarcSymbolpathConfigName
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider

const val GENERATOR_DEPENDENCY_CONFIG_NAME = "arc2fdGenerator"

const val ARC2FD_TOOL_CLASS = "montiarc.arc2fd.Arc2FDTool"

const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:arc2fd:${VERSION}"

val SourceSet.compileArc2FDTaskName: String
  get() = getCompileTaskName("arc2fd")

@Suppress("unused")
@Incubating
class Arc2FDPlugin : Plugin<Project> {
  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    this.project.extensions.extraProperties.set("Arc2FDTaskType", Arc2FDCompile::class.java)

    this.project.pluginManager.apply(MontiArcBasePlugin::class.java)

    with(project) {
      addGeneratorDependency()

      getSourceSetsOf(project).all { sourceSet ->
        val generateTask = createCompileArc2FDTask(sourceSet)

        // feature diagrams are no input of the java compilation, but an artifact of their own
        if (SourceSet.isMain(sourceSet)) {
          tasks.named("assemble") { it.dependsOn(generateTask) }
        }
      }
    }
  }

  private fun addGeneratorDependency() = with(project) {
    configurations.create(GENERATOR_DEPENDENCY_CONFIG_NAME) {
      it.isCanBeResolved = true
      it.isCanBeConsumed = false
    }

    dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, provider { MAVEN_GENERATOR_PROJECT_REF })
  }

  private fun getSourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  private fun createCompileArc2FDTask(sourceSet: SourceSet): TaskProvider<Arc2FDCompile> = with(project) {
    val montiarcSrcDirSet = sourceSet.montiarc.get()
    val taskName = sourceSet.compileArc2FDTaskName
    val generateTask = tasks.register(taskName, Arc2FDCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates feature diagrams from the MontiArc components in source set ${sourceSet.name}."

      genTask.componentPath.setFrom(montiarcSrcDirSet.sourceDirectories)
      genTask.symbolPath.from(
        configurations.named(sourceSet.montiarcSymbolpathConfigName),
        configurations.named(sourceSet.cd2pojo4montiarcSymbolpathConfigName),
        configurations.named(sourceSet.fmu2arc4montiarcSymbolpathConfigName),
        configurations.named(sourceSet.fmu2arc4montiarcCompSymbolpathConfigName)
      )
      genTask.outputDir.set(project.layout.buildDirectory.dir("arc2fd/${sourceSet.name}"))
    }

    return generateTask
  }
}
