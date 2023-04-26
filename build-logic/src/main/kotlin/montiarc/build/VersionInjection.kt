/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register
import java.nio.file.Files

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
    fun Project.registerVersionInjection(taskName: String, genDir: String, packageName: String, constantName: String) {
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
  }
}