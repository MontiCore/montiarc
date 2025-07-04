/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*

/**
 * A task that generates MontiArc components from sequence diagrams, using sd2arc.
 */
abstract class Sd2ArcCompile : JavaExec() {

  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  abstract val modelPath : ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:Optional
  abstract val symbolImportDir : ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

  @get:Input
  abstract val useClass2Mc : Property<Boolean>

  @get:Input
  @get:Optional
  abstract val defaultTicks : Property<Long>

  init {
    description = "Generates .arc components from sequence diagrams using sd2arc."

    classpath(project.configurations.getByName(GENERATOR_DEPENDENCY_CONFIG_NAME))
    mainClass.convention(SD2ARC_TOOL_CLASS)

    useClass2Mc.convention(false)
  }

  fun montiarcOutputDir(): Provider<Directory> {
    return this.outputDir.dir("montiarc")
  }

  @TaskAction
  override fun exec() {

    // printInfo()

    // Delete all outputs to avoid cases such as: user deletes the HWC class, but the generated TOP class persists
    project.delete(outputDir)

    // 1) For directories: filter out entries that do not exist
    val cleanModelPath = getExistingEntriesInProjectFrom(this.modelPath)
    val cleanSymbolImportDirs = getExistingEntriesInProjectFrom(this.symbolImportDir)

    // 2) Build args for the sd2arc generator
    args("--coco")
    args("--input", cleanModelPath.asPath)
    args("--output", this.montiarcOutputDir().get().asFile.path)

    if (!cleanSymbolImportDirs.isEmpty) {
      args("-path", cleanSymbolImportDirs.asPath)
    }

    if(useClass2Mc.get()) { args("--class2mc"); }

    if(defaultTicks.isPresent) { args("--defaultTicks", defaultTicks.get()); }

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

  private fun printInfo() {
    println("Trying generation")

    println("Modelpath:")
    modelPath.forEach { println("  $it") }
    println("Modelpath with existing entries:")
    modelPath.filter { it.exists() }.forEach { println("  $it") }

    println("Symbol import dir:")
    symbolImportDir.forEach { println("  $it") }
    println("Symbol import dir with existing entries:")
    symbolImportDir.filter { it.exists() }.forEach { println("  $it") }

    println("OutDir: " + outputDir.get())

    println("MainClass:" + mainClass.get())
    println("ClassPath:")
    classpath.asPath.split(":").forEach { println("  $it") }
  }
}