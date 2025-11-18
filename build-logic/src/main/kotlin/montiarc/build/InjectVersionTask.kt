/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

import java.nio.charset.StandardCharsets
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class InjectVersionTask : DefaultTask() {

  @get:Input
  abstract val version: Property<String>

  @get:Input
  abstract val pkg: Property<String>

  @get:OutputDirectory
  abstract val target: DirectoryProperty

  @TaskAction
  fun generateBuildInfo() {
    description = "Creates a source file containing this gradle build's version. " +
        "That version can be used by the projects code."

    val content = """
          /* (c) https://github.com/MontiCore/monticore */
          package ${pkg.get()}
      
          const val VERSION = "${version.get()}"
        """.trimIndent()

    val target = this.target.get().asFile

    // The parent directories of the file will be created if they do not exist
    FileUtils.writeStringToFile(target.resolve("Version.kt"), content, StandardCharsets.UTF_8)
  }
}
