/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.fmu2arc

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import org.gradle.work.InputChanges

/**
 * A task that generates Java code from fmu files, using fmu2arc.
 */
@CacheableTask
abstract class FMU2ArcCompile : DefaultTask()  {

  @get:Inject
  abstract val execOps: ExecOperations

  @get:Inject
  abstract val fs: FileSystemOperations

  @get:Inject
  abstract val objectFactory: ObjectFactory

  @get:Internal
  abstract val projectDirectory: DirectoryProperty

  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val modelpath : ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir : DirectoryProperty

  @get:Input
  abstract val printTaskInfo : Property<Boolean>

  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val toolPath : ConfigurableFileCollection

  /** Enable debugging of the FMU2ArcTool while executing*/
  @get:Input
  @get:Option(
    option = "debugTask",
    description = "Enable debugging of the FMU2ArcTool while executing. " +
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
    description = "Generates .java code from fmu files, using fmu2arc."

    debugTask.convention(false)
    debugPort.convention("5005")

    printTaskInfo.convention(false)

    toolPath.setFrom(project.configurations.named(TOOL_CLASSPATH_CONFIG_NAME))
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
      fs.delete { it.delete(this.outputDir) }
    }

    if (printTaskInfo.get()) {
      printInfo()
    }

    // For directories: filter out entries that do not exist
    val cleanModelpath = getExistingEntriesInProjectFrom(this.modelpath)

    if (cleanModelpath.isEmpty) {
      logger.info("None of the given model path directories exists: ${this.modelpath.files}")
      return
    }
    // Delete all outputs
    fs.delete {
      it.delete(outputDir)
    }

    execOps.javaexec {
      it.mainClass.set(getMainClass())
      it.classpath(this.toolPath)

      if (debugTask.get()) {
        it.jvmArgs(
          "-Xdebug",
          "-Xrunjdwp:transport=dt_socket,server=y,address=${debugPort.get()},suspend=y"
        )
      }

      it.args("--input", cleanModelpath.asPath)
      it.args("--output", this.javaOutputDir().get().asFile.path)
      it.args("--symboltable", this.symbolOutputDir().get().asFile.path)
    }
  }

  private fun getMainClass() = FMU2ARC_TOOL_CLASS

  //Custom Method to prevent generator from generating new java files from fmu in jar
  private fun getExistingEntriesInProjectFrom(fileCollection: FileCollection): FileCollection {
    val projectDir = projectDirectory.get().asFile
    return objectFactory.fileCollection().from(
      fileCollection.files.filter { it.exists() && it.startsWith(projectDir) }
    )
  }

  private fun printInfo() {
    println("Trying generation")

    println("Modelpath:")
    modelpath.forEach { println("  $it") }
    println("Modelpath with existing entries:")
    modelpath.filter { it.exists() }.forEach { println("  $it") }


    println("OutDir: " + outputDir.get())

    println("MainClass:" + getMainClass())
    println("ClassPath:")
    this.toolPath.asPath.split(":").forEach { println("  $it") }

    println("Debugging infos: isEnabled=${debugTask.get()}; port=${debugPort.get()}")
  }
}
