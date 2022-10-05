/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test

class MontiarcPlugin : Plugin<Project> {

  companion object Constants {
    const val configName = "maGenerator"
    const val taskName = "compileMontiarc"  // TODO: refactor this to include the sourceSetName (if it's not "main") SourceSet.MAIN_SOURCESET_NAM
    const val dslExtensionName = "montiarc"
    const val maToolClass = "montiarc.generator.MontiArcTool"
    const val internalGeneratorProjectRef = ":generators:ma2java"
    const val mavenGeneratorProjectRef = "montiarc.generators:ma2java"
  }

  override fun apply(project: Project) {
    val extension = project.extensions.create(dslExtensionName, MAExtension::class.java)

    // Add a configuration to store the classpath for executing the ma2java generator
    project.configurations.maybeCreate(configName)

    // Add a dependency on the ma2java jar. Depending on what the user wishes, ma2java may be drawn from maven
    // (default), or it may be an internal project dependency. This only makes sense for us, the MontiArc
    // developers, because this way we can directly test the freshly compiled version of ma2java.
    project.dependencies.addProvider(configName, project.provider {
      if(extension.internalMontiArcTesting.get()) {
        project.project(internalGeneratorProjectRef)
      } else {
        "${mavenGeneratorProjectRef}:${project.version}"
      }
    })

    // Add compileMontiarc task to the project and configure the task
    val generateTask = project.tasks.register(taskName, MontiarcCompile::class.java)
    configure(generateTask, project, extension)
    setTaskOrderAfterMaGenerator(generateTask, project, extension)
  }

  private fun configure(generateTask: TaskProvider<MontiarcCompile>, project: Project, config: MAExtension) {
    generateTask.configure { genTask ->
      // We intentionally use setFrom() as a method on file collections to overwrite the default, so that the user
      // is not forced to use our defaults.
      if (!config.modelPath.isEmpty) {
        genTask.modelPath.setFrom(config.modelPath)
      }
      if(!config.symbolImportDir.isEmpty) {
        genTask.symbolImportDir.setFrom(config.symbolImportDir)
      }
      if (!config.hwcPath.isEmpty) {
        genTask.hwcPath.setFrom(config.hwcPath)
      }
      if(config.outputDir.isPresent) {
        genTask.outputDir.value(outputDirAsProperty(config.outputDir, project))
      }
      if (config.sourceSetName.isPresent) {
        genTask.sourceSetName.set(config.sourceSetName)
      }
      if (config.useClass2Mc.isPresent) {
        genTask.useClass2Mc.set(config.useClass2Mc)
      }
    }
  }

  private fun outputDirAsProperty(outputDir: Provider<String>, project: Project): Provider<Directory> {
    return project.layout.projectDirectory.dir(outputDir)
  }

  private fun setTaskOrderAfterMaGenerator(generateTask: TaskProvider<MontiarcCompile>,
                                           project: Project,
                                           config: MAExtension) {
    generateTask.configure { genTask ->
      if (config.internalMontiArcTesting.get()) {
        genTask.mustRunAfter(project.project(internalGeneratorProjectRef).tasks.withType(Test::class.java))
      }
    }
  }
}

