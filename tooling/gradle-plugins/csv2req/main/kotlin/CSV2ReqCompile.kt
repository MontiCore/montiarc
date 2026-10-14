/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.csv2req

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations

/**
 * A task that translates the IDs of requirements given as CSV files into
 * requirement symbol tables (.reqsym), using csv2req.
 */
@CacheableTask
abstract class CSV2ReqCompile : DefaultTask() {

  @get:Inject
  abstract val execOps: ExecOperations

  @get:Inject
  abstract val fs: FileSystemOperations

  /**
   * The CSV files to translate. Exactly these files are passed to csv2req.
   * The path of a file relative to its source root determines the package of
   * its requirements.
   */
  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val csvFiles: ConfigurableFileCollection

  /**
   * The source directories containing the [csvFiles]. They determine the
   * packages of the requirements, which is tracked by the relative paths of
   * the [csvFiles].
   */
  @get:Internal
  abstract val sourceRoots: ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  /**
   * The (case-insensitive) header names of the column containing the
   * requirement IDs. The first name present in a CSV file is used.
   */
  @get:Input
  abstract val idColumns: ListProperty<String>

  /**
   * The character separating the values, e.g., `;` for CSV files exported by
   * spreadsheet applications in some locales. Defaults to `,`.
   */
  @get:Input
  abstract val delimiter: Property<String>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val toolPath: ConfigurableFileCollection

  /** Enable debugging of the CSV2ReqTool while executing */
  @get:Input
  @get:Option(
    option = "debugTask",
    description = "Enable debugging of the CSV2ReqTool while executing. " +
        "Set the port to which the debugger listens with '--debugPort=...'"
  )
  abstract val debugTask: Property<Boolean>

  @get:Input
  @get:Option(
    option = "debugPort",
    description = "If '--debugTask' is specified, use this option to specify " +
        "to which port the debugger shall listen. Defaults to 5005."
  )
  abstract val debugPort: Property<String>

  init {
    description = "Translates requirements CSV files into requirement symbol tables, using csv2req."

    idColumns.convention(listOf(DEFAULT_ID_COLUMN))
    delimiter.convention(DEFAULT_DELIMITER)

    debugTask.convention(false)
    debugPort.convention("5005")

    toolPath.setFrom(project.configurations.named(TOOL_CLASSPATH_CONFIG_NAME))
  }

  @TaskAction
  fun exec() {
    // The tool does not remove symbol tables of deleted CSV files, hence, we always start clean
    fs.delete { it.delete(this.outputDir) }

    // The arguments are passed as argument file, as the number of CSV files may exceed the command line limits
    val argumentFile = temporaryDir.resolve("arguments.txt")
    argumentFile.writeText(this.arguments().joinToString(System.lineSeparator()), Charsets.UTF_8)

    execOps.javaexec {
      it.mainClass.set(CSV2REQ_TOOL_CLASS)
      it.classpath(this.toolPath)

      if (debugTask.get()) {
        it.jvmArgs(
          "-Xdebug",
          "-Xrunjdwp:transport=dt_socket,server=y,address=${debugPort.get()},suspend=y"
        )
      }

      it.args("@${argumentFile.absolutePath}")
    }
  }

  private fun arguments(): List<String> = buildList {
    add("--input")
    addAll(csvFiles.files.map { it.absolutePath }.sorted())
    val roots = sourceRoots.files.filter { it.isDirectory }
    if (roots.isNotEmpty()) {
      add("--source-root")
      addAll(roots.map { it.absolutePath })
    }
    add("--symboltable")
    add(outputDir.get().asFile.absolutePath)
    add("--id-column")
    addAll(idColumns.get())
    add("--delimiter")
    add(delimiter.get())
  }
}
