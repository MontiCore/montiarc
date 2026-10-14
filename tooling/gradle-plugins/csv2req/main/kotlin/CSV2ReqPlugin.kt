/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.csv2req

import java.util.Locale
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider

const val TOOL_CLASSPATH_CONFIG_NAME = "csv2reqToolClasspath"

const val CSV2REQ_TOOL_CLASS = "montiarc.csv2req.CSV2ReqTool"

const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:csv2req:${VERSION}"

const val DEFAULT_ID_COLUMN = "ID"

const val DEFAULT_DELIMITER = ","

/**
 * Adds a `csv2req` source directory (`src/<sourceSet>/csv2req`) to every source
 * set and a task (`compile<SourceSet>Csv2req`) translating the requirements CSV
 * files therein into requirement symbol tables (.reqsym), located at
 * `build/csv2req/<sourceSet>`. The task runs as part of the source set's
 * `classes` task.
 *
 * The delimiter and the candidate names of the column containing the
 * requirement IDs are configurable per task. The first ID column name present
 * in a CSV file is used:
 * ```
 * tasks.compileCsv2req {
 *   idColumns.set(listOf("ID", "Key"))
 *   delimiter.set(";")
 * }
 * ```
 */
@Suppress("unused")
@Incubating
class CSV2ReqPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with(project) {
      pluginManager.apply(JavaBasePlugin::class.java)

      addGeneratorDependency()

      sourceSetsOf(project).all { sourceSet ->
        addCSV2ReqEntryToSourceSet(sourceSet)
        createCompileCSV2ReqTask(sourceSet)
      }
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  /**
   * Adds the entry "csv2req" to every source set where users can put requirements CSV files.
   */
  private fun addCSV2ReqEntryToSourceSet(sourceSet: SourceSet) {
    val srcDirSet = sourceSet.extensions.create(
      CSV2ReqSourceDirectorySet::class.java, "csv2req",
      DefaultCSV2ReqSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("csv2req", "${sourceSet.name} csv2req source"),
      DefaultTaskDependencyFactory.withNoAssociatedProject()
    )

    // Setting default values for the SourceDirectorySet
    val destinationDir = project.layout.buildDirectory.dir("csv2req/${sourceSet.name}")
    srcDirSet.destinationDirectory.convention(destinationDir)
    srcDirSet.srcDir(project.file("src/${sourceSet.name}/csv2req"))
    // The file extension is matched case-insensitively, consistent with csv2req
    srcDirSet.filter.include { it.isDirectory || it.name.lowercase(Locale.ROOT).endsWith(".csv") }

    sourceSet.allSource.source(srcDirSet)
  }

  private fun addGeneratorDependency() = with(project) {
    // Add a configuration to store the classpath for executing the csv2req generator
    configurations.create(TOOL_CLASSPATH_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that gradle can actually find a jar artifact
      it.isCanBeConsumed = false  // The configuration should not be published, its internal to the compile task impl
      it.isCanBeDeclared = false // The user should not be able to declare new dependencies in this configuration
      it.isVisible = false // Should not be visible outside the project
    }

    // Add a dependency on the csv2req jar
    dependencies.addProvider(TOOL_CLASSPATH_CONFIG_NAME, provider { MAVEN_GENERATOR_PROJECT_REF })
  }

  /**
   * Creates a task that translates the requirements CSV files of the specified source set.
   */
  private fun createCompileCSV2ReqTask(sourceSet: SourceSet): TaskProvider<CSV2ReqCompile> = with(project) {
    val csvSrcDirSet = sourceSet.extensions.getByType(CSV2ReqSourceDirectorySet::class.java)
    val generateTask = tasks.register(sourceSet.compileCSV2ReqTaskName, CSV2ReqCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Translates the requirements CSV files in source set ${sourceSet.name} " +
          "into requirement symbol tables."
      genTask.csvFiles.setFrom(csvSrcDirSet)
      genTask.sourceRoots.setFrom(csvSrcDirSet.sourceDirectories)
      genTask.outputDir.set(csvSrcDirSet.destinationDirectory)
    }

    sourceSet.csv2req.get().compiledBy(generateTask, CSV2ReqCompile::outputDir)
    tasks.named(sourceSet.classesTaskName) { it.dependsOn(generateTask) }

    return generateTask
  }
}
