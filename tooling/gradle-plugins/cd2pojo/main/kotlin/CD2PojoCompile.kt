/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
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
 * Generates Java source code and symbol files from MontiCore class diagrams using
 * the CD2Pojo tool.
 *
 * The task always regenerates all outputs when it executes. This prevents stale
 * generated artifacts, for example a TOP class remaining after its corresponding
 * handwritten class has been removed.
 */
@CacheableTask
abstract class CD2PojoCompile : DefaultTask() {

  @get:Inject
  abstract val execOps: ExecOperations

  @get:Inject
  abstract val fsOps: FileSystemOperations

  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val modelpath: ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val symbolpath: ConfigurableFileCollection

  @get:Input
  abstract val useClass2Mc: Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val hwcPath: ConfigurableFileCollection

  @get:InputDirectory
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:IgnoreEmptyDirectories
  abstract val templateDir: DirectoryProperty

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  /**
   * Starts CD2Pojo with a JDWP debugger attached, suspends until a debugger connects.
   */
  @get:Input
  @get:Option(
    option = "debugTask",
    description = "Start CD2Pojo suspended for remote debugging. " +
        "Use '--debugPort=...' to configure the listening port."
  )
  abstract val debugTask: Property<Boolean>

  /**
   * JDWP port used when [debugTask] is enabled.
   */
  @get:Input
  @get:Option(
    option = "debugPort",
    description = "JDWP listening port for '--debugTask'. Defaults to 5005."
  )
  abstract val debugPort: Property<String>

  @get:Input
  abstract val printTaskInfo: Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val toolClasspath: ConfigurableFileCollection

  @get:Input
  @get:Optional
  abstract val configTemplate: Property<String>

  init {
    description = "Generates Java code from class diagrams using CD2Pojo."

    useClass2Mc.convention(false)

    printTaskInfo.convention(false)

    debugTask.convention(false)
    debugPort.convention("5005")

    toolClasspath.setFrom(project.configurations.named(TOOL_CLASSPATH_CONFIG_NAME))
  }

  fun javaOutputDir(): Provider<Directory> {
    return outputDir.dir("java")
  }

  fun symbolOutputDir(): Provider<Directory> {
    return outputDir.dir("symbols")
  }

  @TaskAction
  fun exec(changes: InputChanges) {
    if (!changes.isIncremental) {
      logger.info("CD2Pojo inputs changed non-incrementally; deleting previous outputs.")
      fsOps.delete { it.delete(outputDir) }
    }

    if (printTaskInfo.get()) {
      printInfo()
    }

    // For directories: filter out entries that do not exist
    val cleanModelpath = getExistingEntriesInProjectFrom(modelpath)
    val cleanSymbolpath = getExistingEntriesInProjectFrom(symbolpath)
    val cleanHwcPath = getExistingEntriesInProjectFrom(hwcPath)

    if (cleanModelpath.isEmpty) {
      logger.info("None of the configured model path entries exists: ${modelpath.files}")
      return
    }

    // Delete all outputs to avoid stale outputs (such as stale TOP-classes)
    fsOps.delete {  it.delete(outputDir) }

    execOps.javaexec {
      it.classpath(toolClasspath)
      it.mainClass.set(getMainClass())

      if (debugTask.get()) {
        it.jvmArgs(
          "-Xdebug",
          "-Xrunjdwp:transport=dt_socket,server=y,address=${debugPort.get()},suspend=y"
        )
      }

      // Set build args for the cd2pojo generator
      it.args("--checkcococs")
      it.args("--input", cleanModelpath.asPath)
      it.args("--output", javaOutputDir().get().asFile.path)
      it.args("--symboltable", symbolOutputDir().get().asFile.path)

      if (useClass2Mc.get()) {
        it.args("--class2mc")
      }

      if (templateDir.isPresent) {
        it.args("--template", templateDir.get())
      }

      if (configTemplate.isPresent) {
        it.args("-ct", configTemplate.get())
      }

      if (!cleanHwcPath.isEmpty) {
        it.args("--handwrittencode", cleanHwcPath.asPath)
      }

      if (!cleanSymbolpath.isEmpty) {
        it.args("-path", cleanSymbolpath.asPath)
      }
    }
  }

  private fun getExistingEntriesInProjectFrom(fileCollection: FileCollection): FileCollection {
    return fileCollection.filter { it.exists() }
  }

  private fun getMainClass() = CD2POJO_TOOL_CLASS

  private fun printInfo() {
    println("Trying generation")

    println("Modelpath:")
    modelpath.forEach { println("  $it") }
    println("Modelpath with existing entries:")
    modelpath.filter { it.exists() }.forEach { println("  $it") }

    println("Symbolpath:")
    symbolpath.forEach { println("  $it") }
    println("Symbolpath with existing entries:")
    symbolpath.filter { it.exists() }.forEach { println("  $it") }

    println("HWCpath:")
    hwcPath.forEach { println("  $it") }

    println("class2mc: " + useClass2Mc.get())

    println("OutputDir: " + outputDir.get())

    println("MainClass:" + getMainClass())
    println("ToolClasspath:")
    toolClasspath.asPath.split(":").forEach { println("  $it") }

    println("Debugging infos: isEnabled=${debugTask.get()}; port=${debugPort.get()}")
  }
}
