/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import org.gradle.work.InputChanges

/**
 * A task that generates MontiArc components from sequence diagrams, using sd2arc.
 */
abstract class SD2ArcCompile : DefaultTask() {

  @get:Inject
  abstract val execOps: ExecOperations

  @get:Inject
  abstract val fs: FileSystemOperations

  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  abstract val modelpath : ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:Optional
  abstract val symbolpath : ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

  @get:Input
  abstract val useClass2Mc : Property<Boolean>

  @get:Input
  @get:Optional
  abstract val defaultTicks : Property<Long>

  /** Enable debugging of the CD2PojoTool while executing*/
  @get:Input
  @get:Option(
    option = "debugTask",
    description = "Enable debugging of the CD2PojoTool while executing. " +
        "Set a the port to which the debugger listens with '--debugPort=...'"
  )
  abstract val debugTask : Property<Boolean>

  @get:Input
  @get:Option(
    option = "debugPort",
    description = "If '--debugTask' is specified, use this option to specify " +
        "to which port the debugger shall listen. Defaults to 5005."
  )
  abstract val debugPort : Property<String>

  @get:Input
  abstract val printTaskInfo : Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val toolPath : ConfigurableFileCollection

  init {
    description = "Generates .arc components from sequence diagrams using sd2arc."

    useClass2Mc.convention(false)

    printTaskInfo.convention(false)

    debugTask.convention(false)
    debugPort.convention("5005")

    toolPath.setFrom(project.configurations.named(TOOL_CLASSPATH_CONFIG_NAME))
  }

  fun montiarcOutputDir(): Provider<Directory> {
    return this.outputDir.dir("montiarc")
  }

  @TaskAction
  fun exec(changes : InputChanges) {
    // We clean the output directory if the task cannot be run incrementally
    if (!changes.isIncremental) {
      fs.delete { it.delete(this.outputDir) }
    }

    if (printTaskInfo.get()) {
      printInfo()
    }

    // For directories: filter out entries that do not exist
    val cleanModelpath = getExistingEntriesInProjectFrom(this.modelpath)
    val cleanSymbolpath = getExistingEntriesInProjectFrom(this.symbolpath)

    // Delete all outputs to avoid cases such as: user deletes the HWC class, but the generated TOP class persists
    fs.delete {  it.delete(outputDir) }

    if (cleanModelpath.isEmpty) {
      logger.info("None of the given model path directories exists: ${this.modelpath.files}")
      return
    }

    execOps.javaexec {
      it.classpath(this.toolPath)
      it.mainClass.convention(getMainClass())

      it.setIgnoreExitValue(true)

      if (debugTask.get()) {
        it.jvmArgs(
          "-Xdebug",
          "-Xrunjdwp:transport=dt_socket,server=y,address=${debugPort.get()},suspend=y"
        )
      }

      // Set build args for the sd2arc generator
      it.args("--coco")
      it.args("--input", cleanModelpath.asPath)
      it.args("--output", this.montiarcOutputDir().get().asFile.path)

      if (!cleanSymbolpath.isEmpty) {
        it.args("-path", cleanSymbolpath.asPath)
      }

      if (useClass2Mc.get()) {
        it.args("--class2mc")
      }

      if (defaultTicks.isPresent) {
        it.args("--defaultTicks", defaultTicks.get())
      }
    }
  }

  private fun getExistingEntriesInProjectFrom(fileCollection: FileCollection): FileCollection {
    return fileCollection.filter { it.exists() }
  }

  private fun getMainClass() = SD2ARC_TOOL_CLASS

  private fun printInfo() {
    println("Trying generation")

    println("Modelpath:")
    modelpath.forEach { println("  $it") }
    println("Modelpath with existing entries:")
    modelpath.filter { it.exists() }.forEach { println("  $it") }

    println("Symbol import dir:")
    symbolpath.forEach { println("  $it") }
    println("Symbol import dir with existing entries:")
    symbolpath.filter { it.exists() }.forEach { println("  $it") }

    println("OutDir: " + outputDir.get())

    println("MainClass:" + getMainClass())
    println("ClassPath:")
    toolPath.asPath.split(":").forEach { println("  $it") }
  }
}
