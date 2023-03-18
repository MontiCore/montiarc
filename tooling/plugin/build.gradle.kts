/* (c) https://github.com/MontiCore/monticore */
import java.nio.file.Files

plugins {
  kotlin("jvm") version "1.5.31"
  id("java-gradle-plugin")
  id("montiarc.build.project-version")
  id("montiarc.build.modules")
  id("montiarc.build.repositories")
  id("montiarc.build.publish-base")
}

group = "montiarc.tooling"

gradlePlugin {
  plugins {
    create("Montiarc") {
      id = "montiarc"
      implementationClass = "montiarc.tooling.plugin.MontiarcPlugin"
    }
  }
}

val genDir4GeneratorVersionInjection = "${project.buildDir}/generatedKotlin"

sourceSets {
  main {
    java {
      setSrcDirs(setOf("main/kotlin", genDir4GeneratorVersionInjection))
    }
    resources {
      setSrcDirs(setOf("main/resources"))
    }
  }
  test {
    java {
      setSrcDirs(setOf("test/kotlin"))
    }
    resources {
      setSrcDirs(setOf("test/resources"))
    }
  }
}

tasks.register<Copy>("injectGeneratorVersion") {
  description = "Creates a source file containing this gradle build's version. " +
      "That version can be used by the plugin code."

  val code = """
    /* (c) https://github.com/MontiCore/monticore */
    package montiarc.tooling.plugin

    const val GENERATOR_VERSION = "${project.version}"
  """.trimIndent()

  // First write the code into a temporary file. This task will then copy it into the build dir of the project.
  // This solution is a bit hacky, but it automatically utilizes gradle's UP-TO-DATE checks.
  val tempDir = Files.createTempDirectory("montiarc")
  Files.write(tempDir.resolve("GeneratorVersion.kt"), code.lines())

  val targetDir = project.file(genDir4GeneratorVersionInjection)

  from(tempDir)
  into(targetDir)
}

tasks.compileKotlin { dependsOn(tasks.getByName("injectGeneratorVersion")) }