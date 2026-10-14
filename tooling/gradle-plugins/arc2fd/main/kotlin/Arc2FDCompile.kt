/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.arc2fd

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations

/**
 * A task that generates feature diagrams from MontiArc components.
 */
@CacheableTask
abstract class Arc2FDCompile : DefaultTask() {

  @get:InputFiles
  @get:SkipWhenEmpty
  @get:IgnoreEmptyDirectories
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val componentPath: ConfigurableFileCollection

  /** Symbols of models that the components depend on, e.g., of other MontiArc libraries. */
  @get:InputFiles
  @get:IgnoreEmptyDirectories
  @get:Optional
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val symbolPath: ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  @get:Classpath
  abstract val classPath: ConfigurableFileCollection

  @get:Input
  @get:Option(
    option = "include-subcomponents",
    description = "If '--include-subcomponents' is present, the arc2fd tool will recurse into subcomponents."
  )
  abstract val includeSubcomponents: Property<Boolean>

  @get:Inject
  abstract val execOperations: ExecOperations

  @get:Inject
  abstract val fsOperations: FileSystemOperations

  init {
    description = "Generates .fd files from MontiArc components using arc2fd"

    includeSubcomponents.convention(false)

    classPath.setFrom(project.configurations.named(GENERATOR_DEPENDENCY_CONFIG_NAME))
  }

  @TaskAction
  fun exec() {
    fsOperations.delete {
      it.delete(outputDir)
    }

    execOperations.javaexec {
      it.classpath(this.classPath)
      it.mainClass.set(ARC2FD_TOOL_CLASS)

      if (includeSubcomponents.get()) {
        it.args("--include-subcomponents")
      }

      it.args("--input", componentPath.filter { f -> f.exists() }.asPath)
      it.args("--output", outputDir.get().asFile.path)

      val existingSymbolPath = symbolPath.filter { f -> f.exists() }
      if (!existingSymbolPath.isEmpty) {
        it.args("-path", existingSymbolPath.asPath)
      }
    }
  }
}
