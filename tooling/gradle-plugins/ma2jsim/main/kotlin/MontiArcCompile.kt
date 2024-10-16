/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*

/**
 * A task that generates Java code from MontiArc models.
 */
abstract class MontiArcCompile : JavaExec() {
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

  init {
    description = "Generates .java code from MontiArc models."

    classpath(project.configurations.getByName(GENERATOR_DEPENDENCY_CONFIG_NAME))
    mainClass.convention(MA_TOOL_CLASS)

    useClass2Mc.convention(false)
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
  override fun exec() {

    // printInfo()

    // 1) For directories: filter out entries that do not exist
    val cleanModelPath = getExistingEntriesInProjectFrom(this.modelPath)
    val cleanSymbolImportDirs = getExistingEntriesInProjectFrom(this.symbolImportDir)
    val cleanHwcPath = getExistingEntriesInProjectFrom(this.hwcPath)

    // 2) Build args for the montiarc generator
    args("--input", cleanModelPath.asPath)
    args("--output", this.javaOutputDir().get().asFile.path)
    args("--symboltable", this.symbolOutputDir().get().asFile.path)
    args("--report", this.reportsOutputDir().get().asFile.path)

    if(useClass2Mc.get()) { args("--class2mc"); }

    if (!cleanHwcPath.isEmpty) { args("--handwritten-code", cleanHwcPath.asPath); }
    if (!cleanSymbolImportDirs.isEmpty) {
      args("-path", cleanSymbolImportDirs.asPath)
    }

    // 3) Execute
    if (cleanModelPath.isEmpty) {
      logger.info("None of the given model path directories exists: ${this.modelPath.files}")
    } else {
      super.exec()

      if (executionResult.get().exitValue == -1) {
        throw GradleException("There are compile errors.")
      }
    }
  }

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

    println("class2mc: " + useClass2Mc.get())

    println("Symbol import dir:")
    symbolImportDir.forEach { println("  $it") }
    println("Symbol import dir with existing entries:")
    symbolImportDir.filter { it.exists() }.forEach { println("  $it") }

    println("OutDir: " + outputDir.get())
    println("Reports out dir: " + reportsOutputDir().get())

    println("MainClass:" + mainClass.get())
    println("ClassPath:")
    classpath.asPath.split(":").forEach { println("  $it") }
  }
}

val SourceSet.compileMontiarcTaskName: String
  get() = getCompileTaskName("montiarc")

