/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
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
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import org.gradle.work.InputChanges

/**
 * A task that generates Java code from MontiArc models.
 */
@CacheableTask
abstract class MontiArcCompile : DefaultTask() {

  @get:Inject
  abstract val execOps: ExecOperations

  @get:Inject
  abstract val fs: FileSystemOperations

  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val modelpath : ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val symbolpath : ConfigurableFileCollection

  @get:Input
  abstract val useClass2Mc : Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val hwcPath: ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

  @get:Input
  abstract val checkVariability : Property<Boolean>

  @get:Input
  abstract val printTaskInfo : Property<Boolean>

  @get:Input
  abstract val debugLog: Property<Boolean>

  @get:Input
  abstract val traceLog: Property<Boolean>

  /** Enable debugging of the CD2PojoTool while executing. */
  @get:Input
  @get:Option(
    option = "debugTask",
    description = "Enable debugging of the MA2JsimTool while executing. " +
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

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val toolPath : ConfigurableFileCollection

  init {
    description = "Generates .java code from MontiArc models."

    useClass2Mc.convention(false)
    checkVariability.convention(false)
    debugLog.convention(false)
    traceLog.convention(false)

    printTaskInfo.convention(false)
    debugTask.convention(false)
    debugPort.convention("5005")

    toolPath.setFrom(project.configurations.named(TOOL_CLASSPATH_CONFIG_NAME))
  }

  fun javaOutputDir(): Provider<Directory> {
    return this.outputDir.dir("java")
  }

  fun symbolOutputDir(): Provider<Directory> {
    return this.outputDir.dir("symbols")
  }

  fun reportsOutputDir(): Provider<Directory> {
    return this.outputDir.dir("reports")
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
    val cleanHwcPath = getExistingEntriesInProjectFrom(this.hwcPath)

    if (cleanModelpath.isEmpty) {
      logger.info("None of the given model path directories exists: ${this.modelpath.files}")
      return
    }

    val exec = execOps.javaexec {
      it.classpath(this.toolPath)
      it.mainClass.set(getMainClass())

      it.setIgnoreExitValue(true)

      // Enable remote debugging when option is set
      if (debugTask.get()) {
        it.jvmArgs(
          "-Xdebug",
          "-Xrunjdwp:transport=dt_socket,server=y,address=${debugPort.get()},suspend=y"
        )
      }

      // Set build args for the montiarc generator
      it.args("--input", cleanModelpath.asPath)
      it.args("--output", this.javaOutputDir().get().asFile.path)
      it.args("--symboltable", this.symbolOutputDir().get().asFile.path)
      it.args("--report", this.reportsOutputDir().get().asFile.path)

      if (debugLog.get()) {
        it.args("--stacktrace=ERROR,WARN,INFO,TRACE,DEBUG")
      } else if (traceLog.get()) {
        it.args("--stacktrace=ERROR,WARN,INFO,TRACE")
      }

      if(useClass2Mc.get()) { it.args("--class2mc") }

      if (!cleanHwcPath.isEmpty) { it.args("--handwritten-code", cleanHwcPath.asPath); }
      if (!cleanSymbolpath.isEmpty) {
        it.args("-path", cleanSymbolpath.asPath)
      }

      if(!checkVariability.get()) { it.args("--no-variability-checks"); }
    }

    if (exec.exitValue != 0) {
      throw GradleException("There are compile errors.")
    }
  }

  private fun getMainClass() = MA_TOOL_CLASS

  private fun getExistingEntriesInProjectFrom(fileCollection: FileCollection): FileCollection {
    return fileCollection.filter { it.exists() }
  }

  private fun printInfo() {
    println("Trying generation")

    println("Modelpath:")
    modelpath.forEach { println("  $it") }
    println("Modelpath with existing entries:")
    modelpath.filter { it.exists() }.forEach { println("  $it") }

    println("HWCpath:")
    hwcPath.forEach { println("  $it") }

    println("Check variability: " + checkVariability.get())

    println("class2mc: " + useClass2Mc.get())

    println("Symbol import dir:")
    symbolpath.forEach { println("  $it") }
    println("Symbol import dir with existing entries:")
    symbolpath.filter { it.exists() }.forEach { println("  $it") }

    println("OutDir: " + outputDir.get())
    println("Reports out dir: " + reportsOutputDir().get())

    println("MainClass:" + getMainClass())
    println("ClassPath:")
    this.toolPath.asPath.split(":").forEach { println("  $it") }
  }
}

val SourceSet.compileMontiArcTaskName: String
  get() = getCompileTaskName("montiarc")

