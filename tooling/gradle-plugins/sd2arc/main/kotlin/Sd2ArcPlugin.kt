/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import montiarc.gradle.montiarc.montiarc
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.lambdas.SerializableLambdas
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider

const val GENERATOR_DEPENDENCY_CONFIG_NAME = "sd2arcGenerator"
const val DSL_EXTENSION_NAME = "sd2arc"

const val SD2ARC_TOOL_CLASS = "de.monticore.sd2arc.SD2ArcTool"

const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:sd2arc:${VERSION}"

const val SE_LOGGING_PROJECT_REF = "de.se_rwth.commons:se-commons-logging:${VERSION}"

@Suppress("unused")
@Incubating
class Sd2ArcPlugin : Plugin<Project> {

  private lateinit var project: Project
  private lateinit var sdExtension: Sd2ArcExtension

  override fun apply(project: Project) {
    this.project = project
    this.sdExtension = project.extensions.create(DSL_EXTENSION_NAME, Sd2ArcExtension::class.java)

    this.project.extensions.extraProperties.set("SDTaskType", Sd2ArcCompile::class.java)

    with (project) {
      pluginManager.apply("java-base")

      addGeneratorDependency()

      getSourceSetsOf(project).all { sourceSet ->
        // Adding an entry for sd2arc to all source sets and creating compile tasks from them
        addSd2ArcEntryToSourceSet(sourceSet)
        createCompileSd2ArcTask(sourceSet)
        dependencies.addProvider(sourceSet.implementationConfigurationName, provider { SE_LOGGING_PROJECT_REF })
      }

      pluginManager.withPlugin("cd2pojo") {
        pluginManager.apply(CDOut2SDInPlugin::class.java)
      }

      pluginManager.withPlugin("montiarc-jsim") {
        pluginManager.apply(MAJsimOut2SDInPlugin::class.java)
      }
    }
  }

  private fun addGeneratorDependency() = with (project) {
    // Add a configuration to store the classpath for executing the sd2arc generator
    configurations.create(GENERATOR_DEPENDENCY_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that gradle can actually find a jar artifact
      it.isCanBeConsumed = false  // We do not use this configuration for publishing
    }

    // Add a dependency on the sd2arc jar
    dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, provider { MAVEN_GENERATOR_PROJECT_REF })
  }

  private fun getSourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  /**
   * Adds the entry "sd2arc" to every source set where users can put sequence diagram models.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * sd2arc sources is added to the MontiArc sources of the same SourceSet
   */
  private fun addSd2ArcEntryToSourceSet(sourceSet: SourceSet) {
    val srcDirSet = sourceSet.extensions.create(
      Sd2ArcSourceDirectorySet::class.java, "sd2arc",
      DefaultSd2ArcSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("sd2arc", "${sourceSet.name} sd2arc source"),
      // DefaultTaskDependencyFactory.withNoAssociatedProject()  // Needed starting with Gradle v.8
    )

    // Setting default values for the SourceDirectorySet
    val destinationDir = project.layout.buildDirectory.dir("sd2arc/${sourceSet.name}")
    srcDirSet.destinationDirectory.convention(destinationDir)
    srcDirSet.srcDir(project.file("src/${sourceSet.name}/sd2arc"))
    srcDirSet.filter.include("**/*.sd")


    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
    // configuration cache.
    // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    val srcDirectorySetAsFileCollection = srcDirSet as FileCollection
    sourceSet.resources.exclude(SerializableLambdas.spec { el -> srcDirectorySetAsFileCollection.contains(el.file) })
    sourceSet.allSource.source(srcDirSet)
  }

  /**
   * Create a task that compiles the class diagram sources of the specified source set.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * task is added to the MontiArc sources of the same SourceSet.
   */
  private fun createCompileSd2ArcTask(sourceSet: SourceSet): TaskProvider<Sd2ArcCompile> = with (project) {
    val sdSrcDirSet = sourceSet.extensions.getByType(Sd2ArcSourceDirectorySet::class.java)
    val taskName = sourceSet.compileSd2ArcTaskName
    val generateTask = tasks.register(taskName, Sd2ArcCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates montiarc components from the sequence diagram models in source set ${sourceSet.name}."

      genTask.modelPath.setFrom(sdSrcDirSet.sourceDirectories)
      genTask.outputDir.set(sdSrcDirSet.destinationDirectory)

      sourceSet.montiarc.ifPresent { ma -> ma.srcDir(genTask.montiarcOutputDir()) }
    }

    sourceSet.sd2arc.get().compiledBy(generateTask, Sd2ArcCompile::outputDir)
    tasks.named(sourceSet.compileJavaTaskName) { it.dependsOn(generateTask) }

    return generateTask
  }
}
