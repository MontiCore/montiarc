/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2java

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
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

/**
 * A task that generates Java code from MontiArc models.
 */
@CacheableTask
abstract class MontiArcCompile : JavaExec() {
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

  @get:Input
  @get:Optional
  abstract val dse : Property<Boolean>

  @get:Input
  abstract val debugLog: Property<Boolean>

  @get:Input
  abstract val traceLog: Property<Boolean>

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

  @get:Input
  abstract val printTaskInfo : Property<Boolean>

  init {
    description = "Generates .java code from MontiArc models."

    classpath(project.configurations.getByName(GENERATOR_DEPENDENCY_CONFIG_NAME))
    mainClass.convention(MA_TOOL_CLASS)

    useClass2Mc.convention(false)
    dse.convention(false)
    printTaskInfo.convention(false)
    debugLog.convention(false)
    traceLog.convention(false)
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

    if (printTaskInfo.get()) {
      printInfo()
    }

    // Expose internal javac API for google-java-format
    jvmArgs(
      listOf(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
      )
    )

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

    if (debugLog.get()) {args("--debug");}
    if (traceLog.get()) {args("--trace");}

    if (!cleanHwcPath.isEmpty) { args("--handwritten-code", cleanHwcPath.asPath); }
    if (!cleanSymbolImportDirs.isEmpty) {
      args("-path", cleanSymbolImportDirs.asPath)
    }

    if(dse.get()){args("-dse");}


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

