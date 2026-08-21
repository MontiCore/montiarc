/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.fmu2arc

import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider

const val TOOL_CLASSPATH_CONFIG_NAME = "fmu2arcToolClasspath"

const val FMU2ARC_TOOL_CLASS = "de.montiarc.generator.FMU2ArcTool"

const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:fmu2arc:${VERSION}"

const val SE_LOGGING_PROJECT_REF = "de.se_rwth.commons:se-commons-logging:${VERSION}"

const val MAVEN_RTE_PROJECT_REF = "montiarc.libraries:simulator-rte:${VERSION}"

const val MAVEN_FMI4J_REF = "io.github.generosolombardi-av.fmi4j:fmi-import:0.38.0-multiplatform.6"

const val MAVEN_SLF4J_REF = "de.se_rwth.commons:se-commons-logging-slf4j:${VERSION}"

@Suppress("unused")
@Incubating
class FMU2ArcPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    this.project.extensions.extraProperties.set("FMUTaskType", FMU2ArcCompile::class.java)

    with(project) {
      addGeneratorDependency()

      sourceSetsOf(project).all { sourceSet ->
        // Adding an entry for fmu2arc to all source sets and creating compile tasks from them
        addFMU2ArcEntryToSourceSet(sourceSet)
        createCompileFMU2ArcTask(sourceSet)
        dependencies.addProvider(sourceSet.implementationConfigurationName, provider { SE_LOGGING_PROJECT_REF })

        dependencies.addProvider(sourceSet.implementationConfigurationName, provider { MAVEN_FMI4J_REF })
        dependencies.addProvider(sourceSet.implementationConfigurationName, provider { MAVEN_RTE_PROJECT_REF })
        dependencies.addProvider(sourceSet.runtimeOnlyConfigurationName, provider { MAVEN_SLF4J_REF })
      }
      pluginManager.apply(FMU2ArcDistributionPlugin::class.java)
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  /**
   * Adds the entry "fmu2arc" to every source set where users can put class diagram models.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * fmu2arc sources is added to the java sources of the same SourceSet
   */
  private fun addFMU2ArcEntryToSourceSet(sourceSet: SourceSet) {
    val srcDirSet = sourceSet.extensions.create(
      FMU2ArcSourceDirectorySet::class.java, "fmu2arc",
      DefaultFMU2ArcSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("fmu2arc", "${sourceSet.name} fmu2arc source"),
      DefaultTaskDependencyFactory.withNoAssociatedProject()
    )

    // Setting default values for the SourceDirectorySet
    val destinationDir = project.layout.buildDirectory.dir("fmu2arc/${sourceSet.name}")
    srcDirSet.destinationDirectory.convention(destinationDir)
    srcDirSet.srcDir(project.file("src/${sourceSet.name}/fmu2arc"))
    srcDirSet.filter.include("**/*.fmu")

    sourceSet.allSource.source(srcDirSet)
  }

  private fun addGeneratorDependency() = with (project) {
    // Add a configuration to store the classpath for executing the fmu2arc generator
    configurations.create(TOOL_CLASSPATH_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that gradle can actually find a jar artifact
      it.isCanBeConsumed = false  // The configuration should not be published, its internal to the compile task impl
      it.isCanBeDeclared = false // The user should not be able to declare new dependencies in this configuration
      it.isVisible = false // Should not be visible outside the project
    }

    // Add a dependency on the fmu2arc jar
    dependencies.addProvider(TOOL_CLASSPATH_CONFIG_NAME, provider { MAVEN_GENERATOR_PROJECT_REF })
    dependencies.addProvider(TOOL_CLASSPATH_CONFIG_NAME, provider { MAVEN_SLF4J_REF })
  }

  /**
   * Create a task that compiles the fmu file sources of the specified source set.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * task is added to the java sources of the same SourceSet.
   */
  private fun createCompileFMU2ArcTask(sourceSet: SourceSet): TaskProvider<FMU2ArcCompile> = with (project) {
    val fmuSrcDirSet = sourceSet.extensions.getByType(FMU2ArcSourceDirectorySet::class.java)
    val taskName = sourceSet.compileFMU2ArcTaskName
    val generateTask = tasks.register(taskName, FMU2ArcCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates java code from the fmu files in source set ${sourceSet.name}."
      genTask.projectDirectory.set(project.layout.projectDirectory) // Prevents generator from generating java code again
      genTask.modelpath.setFrom(fmuSrcDirSet.sourceDirectories)
      genTask.outputDir.set(fmuSrcDirSet.destinationDirectory)

      sourceSet.java.srcDir(genTask.javaOutputDir())
      // Add the FMU files to the resources so they are bundled in the output JAR
      sourceSet.resources.srcDir(fmuSrcDirSet.sourceDirectories)
    }


    sourceSet.fmu2arc.get().compiledBy(generateTask, FMU2ArcCompile::outputDir)
    tasks.named(sourceSet.compileJavaTaskName) { it.dependsOn(generateTask) }

    return generateTask
  }

}
