/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
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
import org.gradle.work.InputChanges

/**
 * A task that generates Java code from class diagrams, using cd2pojo.
 */
@CacheableTask
abstract class Cd2PojoCompile : DefaultTask() {

  // Unimplemented options: help / version / prettyprint / reports / turning cocos off / configtemplate /templatepath
  // see CD4CodeTool#addStandartOptions
  //     CDGeneratorTool#addAdditionalOptions

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
  abstract val hwcPath : ConfigurableFileCollection

  @get:InputDirectory
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  @get:IgnoreEmptyDirectories
  abstract val tmplDir : DirectoryProperty

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

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
  abstract val classPath : ConfigurableFileCollection

  init {
    description = "Generates .java code from class diagrams using cd2pojo."

    useClass2Mc.convention(false)

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

  @TaskAction
  fun exec(changes : InputChanges) {
    // We clean the output directory if the task cannot be run incrementally
    if (!changes.isIncremental) {
      this.outputDir.get().asFile.deleteRecursively()
    }

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

    // Delete all outputs to avoid cases such as: user deletes the HWC class, but the generated TOP class persists
    project.delete(outputDir)

    project.javaexec {
      it.classpath(this.classPath)
      it.mainClass.set(getMainClass())

      if (debugTask.get()) {
        it.jvmArgs(
          "-Xdebug",
          "-Xrunjdwp:transport=dt_socket,server=y,address=${debugPort.get()},suspend=y"
        )
      }

      // Set build args for the cd2pojo generator
      it.args("--checkcococs")
      it.args("--input", cleanModelPath.asPath)
      it.args("--output", this.javaOutputDir().get().asFile.path)
      it.args("--symboltable", this.symbolOutputDir().get().asFile.path)

      if (useClass2Mc.get()) {
        it.args("--class2mc")
      }

      if (tmplDir.isPresent) {
        it.args("--template", tmplDir.get())
      }

      if (!cleanHwcPath.isEmpty) {
        it.args("--handwrittencode", cleanHwcPath.asPath)
      }
      if (!cleanSymbolImportDirs.isEmpty) {
        it.args("-path", cleanSymbolImportDirs.asPath)
      }
    }
  }

  private fun getExistingEntriesInProjectFrom(fileCollection: FileCollection): FileCollection {
    return project.files(
      fileCollection.files.filter { it.exists() }
    )
  }

  private fun getMainClass() = CD2POJO_TOOL_CLASS

  private fun printInfo() {
    println("Trying generation")

    println("Modelpath:")
    modelPath.forEach { println("  $it") }
    println("Modelpath with existing entries:")
    modelPath.filter { it.exists() }.forEach { println("  $it") }

    println("HWCpath:")
    hwcPath.forEach { println("  $it") }

    println("class2mc: " + useClass2Mc.get())

    println("Symbol import dir:")
    symbolImportDir.forEach { println("  $it") }
    println("Symbol import dir with existing entries:")
    symbolImportDir.filter { it.exists() }.forEach { println("  $it") }

    println("OutDir: " + outputDir.get())

    println("MainClass:" + getMainClass())
    println("ClassPath:")
    this.classPath.asPath.split(":").forEach { println("  $it") }

    println("Debugging infos: isEnabled=${debugTask.get()}; port=${debugPort.get()}")
  }
}
