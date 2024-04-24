/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime

class VersionInjection {
  companion object{
    /**
     * Generates code with a constant holding the current project version. To this end, a task is registered.
     *
     * @param taskName name of the task to register.
     * @param genDir name of the directory to which the kotlin file should be generated. Is interpreted relatively
     *               from the project location
     * @param packageName the package name of the generated Kotlin file
     * @param constantName the name of the constant that will contain the version number.
     */
    fun Project.registerVersionInjectionForPlugins(taskName: String,
                                                   genDir: String,
                                                   packageName: String,
                                                   constantName: String) {
      tasks.register<Copy>(taskName) {
        description = "Creates a source file containing this gradle build's version. " +
          "That version can be used by the projects code."

        val code = """
          /* (c) https://github.com/MontiCore/monticore */
          package $packageName
      
          const val $constantName = "${project.version}"
        """.trimIndent()

        // First write the code into a temporary file. This task will then copy it into the build dir of the project.
        // This solution is a bit hacky, but it automatically utilizes gradle's UP-TO-DATE checks.
        val tempDir = Files.createTempDirectory("montiarc")
        Files.write(tempDir.resolve("GeneratorVersion.kt"), code.lines())

        from(tempDir)
        into(project.file(genDir))
      }
    }

    fun Project.registerVersionInjectionForUpToDateChecks(taskName: String,
                                                          genDir: String,
                                                          subfolder: String,
                                                          fileName: String) {
      var versionString = project.version.toString()
      if (project.version.toString().contains("SNAPSHOT")) {
        versionString += "#" + ZonedDateTime.now().hashCode()
      }

      val copyTask = tasks.register<Copy>(taskName) {
        description = "Serializes the version number of the generator to a resource file so that it can be read during" +
          "the runtime of the tool, e.g. to consider it during up to date checking"

        // First write the version into a temporary file. This task will then copy it into the build dir of the project.
        // This solution is a bit hacky, but it automatically utilizes gradle's UP-TO-DATE checks.
        val tempDir = Files.createTempDirectory("montiarc")
        Files.write(tempDir.resolve(fileName), versionString.lines())

        from(tempDir)
        into(project.file(Path.of(genDir).resolve(subfolder)))
      }

      pluginManager.withPlugin("java") {
        val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        javaExtension.sourceSets.named("main").configure { resources.srcDir(genDir) }
        tasks.named("compileJava").configure { finalizedBy(copyTask) }
        tasks.named("processResources").configure { dependsOn(copyTask) }

        copyTask.configure {
          onlyIf { tasks.getByName("compileJava").state.upToDate.not() }
        }
      }
    }
  }
}