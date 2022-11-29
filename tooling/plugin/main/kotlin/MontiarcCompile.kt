/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

/**
 * A task that generates Java code from Montiarc models.
 */
abstract class MontiarcCompile : JavaExec() {
  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  abstract val modelPath : ConfigurableFileCollection

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  abstract val symbolImportDir : ConfigurableFileCollection

  @get:Input
  abstract val useClass2Mc : Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  abstract val hwcPath : ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

  @Deprecated("You can now directly declare montiarc sources in the sourceSet { main { montiarc {...} } } block")
  @get:Input
  @get:Optional
  abstract val sourceSetName: Property<String>

  init {
    description = "Generates .java code from MontiArc models."

    classpath(project.configurations.getByName(GENERATOR_DEPENDENCY_CONFIG_NAME))
    mainClass.convention(MA_TOOL_CLASS)

    useClass2Mc.convention(false)
  }

  @TaskAction
  override fun exec() {

    //printInfo()

    // 1) For the model path and symbol import directories: filter out directories that do not exist
    val cleanModelPath = project.files(
      this.modelPath.files.filter { project.file(it).exists() }
    )
    val cleanSymbolImportDirs = project.files(
      this.symbolImportDir.files.filter { project.file(it).exists() }
    )

    // 2) Build args for the montiarc generator
    args("-mp", cleanModelPath.asPath)
    args("-o", this.outputDir.asFile.get().path)
    args("-hwc", this.hwcPath.asPath)

    if(useClass2Mc.get()) { args("-c2mc"); }

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

  private fun printInfo() {
    println("Trying generation")
    println("Modelpath:")
    modelPath.forEach { println("  $it") }
    println("HWCpath:")
    hwcPath.forEach { println("  $it") }
    println("class2mc: " + useClass2Mc.get())
    println("Symbol import dir:")
    symbolImportDir.forEach { println("  $it") }
    println("OutDir: " + outputDir.get())

    println("MainClass:" + mainClass.get())
    println("ClassPath:")
    classpath.asPath.split(":").forEach { println("  $it") }
  }
}