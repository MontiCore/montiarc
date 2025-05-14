/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

/**
 * A task that generates Java code from class diagrams, using cd2pojo.
 */
abstract class Cd2PojoCompile : JavaExec() {

  // Unimplemented options: help / version / prettyprint / reports / turning cocos off / configtemplate /templatepath
  // see CD4CodeTool#addStandartOptions
  //     CDGeneratorTool#addAdditionalOptions

  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  abstract val modelPath : ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:Optional
  abstract val symbolImportDir : ConfigurableFileCollection

  @get:Input
  abstract val useClass2Mc : Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  abstract val hwcPath : ConfigurableFileCollection

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

  init {
    description = "Generates .java code from class diagrams using cd2pojo."

    classpath(project.configurations.getByName(GENERATOR_DEPENDENCY_CONFIG_NAME))
    mainClass.convention(CD2POJO_TOOL_CLASS)

    useClass2Mc.convention(false)

    debugTask.convention(false)
    debugPort.convention("5005")
  }

  fun javaOutputDir(): Provider<Directory> {
    return this.outputDir.dir("java")
  }

  fun symbolOutputDir(): Provider<Directory> {
    return this.outputDir.dir("symbols")
  }

  @TaskAction
  override fun exec() {

    // printInfo()

    // Delete all outputs to avoid cases such as: user deletes the HWC class, but the generated TOP class persists
    project.delete(outputDir)

    // Enable remote debugging when option is set
    if (debugTask.get()) {
      enableDebugging()
    }

    // 1) For directories: filter out entries that do not exist
    val cleanModelPath = getExistingEntriesInProjectFrom(this.modelPath)
    val cleanSymbolImportDirs = getExistingEntriesInProjectFrom(this.symbolImportDir)
    val cleanHwcPath = getExistingEntriesInProjectFrom(this.hwcPath)

    // 2) Build args for the cd2pojo generator
    args("--checkcococs")
    args("--input", cleanModelPath.asPath)
    args("--output", this.javaOutputDir().get().asFile.path)
    args("--symboltable", this.symbolOutputDir().get().asFile.path)

    if(useClass2Mc.get()) { args("--class2mc"); }

    if (!cleanHwcPath.isEmpty) { args("--handwrittencode", cleanHwcPath.asPath); }
    if (!cleanSymbolImportDirs.isEmpty) {
      args("-path", cleanSymbolImportDirs.asPath)
    }

    // 3) Execute
    if (cleanModelPath.isEmpty) {
      logger.info("None of the given model path directories exists: ${this.modelPath.files}")
    } else {
      super.exec()
    }
  }

  private fun getExistingEntriesInProjectFrom(fileCollection: FileCollection): FileCollection {
    return project.files(
      fileCollection.files.filter { it.exists() }
    )
  }

  private fun enableDebugging() {
    jvmArgs(
      "-Xdebug",
      "-Xrunjdwp:transport=dt_socket,server=y,address=${debugPort.get()},suspend=y"
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

    println("class2mc: " + useClass2Mc.get())

    println("Symbol import dir:")
    symbolImportDir.forEach { println("  $it") }
    println("Symbol import dir with existing entries:")
    symbolImportDir.filter { it.exists() }.forEach { println("  $it") }

    println("OutDir: " + outputDir.get())

    println("MainClass:" + mainClass.get())
    println("ClassPath:")
    classpath.asPath.split(":").forEach { println("  $it") }

    println("Debugging infos: isEnabled=${debugTask.get()}; port=${debugPort.get()}")
  }
}