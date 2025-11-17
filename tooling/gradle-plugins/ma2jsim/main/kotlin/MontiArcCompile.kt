/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
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

/**
 * A task that generates Java code from MontiArc models.
 */
@CacheableTask
abstract class MontiArcCompile : DefaultTask() {
  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val modelPath : ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val symbolImportDir : ConfigurableFileCollection

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

  @get:Input
  abstract val fileLog: Property<Boolean>

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
  abstract val classPath : ConfigurableFileCollection

  init {
    description = "Generates .java code from MontiArc models."

    useClass2Mc.convention(false)
    checkVariability.convention(false)
    debugLog.convention(false)
    traceLog.convention(false)
    fileLog.convention(false)

    printTaskInfo.convention(false)
    debugTask.convention(false)
    debugPort.convention("5005")

    classPath.setFrom(project.configurations.named(GENERATOR_DEPENDENCY_CONFIG_NAME))
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
  fun exec() {
    if (printTaskInfo.get()) {
      printInfo()
    }

    // For directories: filter out entries that do not exist
    val cleanModelPath = getExistingEntriesInProjectFrom(this.modelPath)
    val cleanSymbolImportDirs = getExistingEntriesInProjectFrom(this.symbolImportDir)
    val cleanHwcPath = getExistingEntriesInProjectFrom(this.hwcPath)

    if (cleanModelPath.isEmpty) {
      logger.info("None of the given model path directories exists: ${this.modelPath.files}")
      return
    }

    val exec = project.javaexec {
      it.classpath(this.classPath)
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
      it.args("--input", cleanModelPath.asPath)
      it.args("--output", this.javaOutputDir().get().asFile.path)
      it.args("--symboltable", this.symbolOutputDir().get().asFile.path)
      it.args("--report", this.reportsOutputDir().get().asFile.path)

      if (debugLog.get()) { it.args("--debug") }
      if (traceLog.get()) { it.args("--trace") }
      if (fileLog.get()) { it.args("--file") }

      if(useClass2Mc.get()) { it.args("--class2mc") }

      if (!cleanHwcPath.isEmpty) { it.args("--handwritten-code", cleanHwcPath.asPath); }
      if (!cleanSymbolImportDirs.isEmpty) {
        it.args("-path", cleanSymbolImportDirs.asPath)
      }

      if(!checkVariability.get()) { it.args("--no-variability-checks"); }
    }

    if (exec.exitValue != 0) {
      throw GradleException("There are compile errors.")
    }
  }

  private fun getMainClass() = MA_TOOL_CLASS

  private fun getExistingEntriesInProjectFrom(fileCollection: FileCollection): FileCollection {
    return project.files(
      fileCollection.files.filter { it.exists() }
    )
  }

  private fun printInfo() {
    println("Trying generation")

    println("Modelpath:")
    modelPath.forEach { println("  $it") }
    println("Modelpath with existing entries:")
    modelPath.filter { it.exists() }.forEach { println("  $it") }

    println("HWCpath:")
    hwcPath.forEach { println("  $it") }

    println("Check variability: " + checkVariability.get())

    println("class2mc: " + useClass2Mc.get())

    println("Symbol import dir:")
    symbolImportDir.forEach { println("  $it") }
    println("Symbol import dir with existing entries:")
    symbolImportDir.filter { it.exists() }.forEach { println("  $it") }

    println("OutDir: " + outputDir.get())
    println("Reports out dir: " + reportsOutputDir().get())

    println("MainClass:" + getMainClass())
    println("ClassPath:")
    this.classPath.asPath.split(":").forEach { println("  $it") }
  }
}

val SourceSet.compileMontiarcTaskName: String
  get() = getCompileTaskName("montiarc")

