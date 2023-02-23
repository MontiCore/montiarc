/* (c) https://github.com/MontiCore/monticore */
buildscript {
  dependencies {
    classpath("montiarc.tooling:plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.modules")
  id("montiarc.build.project-version")

  id("montiarc")  // Implicitly applies java-base
}

group = "montiarc.tooling.plugin-itest"

val fooSourceSet = sourceSets.create("foo")
val fooImplConfig = configurations.getByName(fooSourceSet.implementationConfigurationName)

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  fooImplConfig(project(":libraries:majava-rte"))
}

val checkGenerationTask = tasks.register("checkCorrectGeneration", CheckFilesArePresent::class.java) {
  dependsOn(tasks.named("compileFooMontiarc"))
  group = "verification"

  val expectedGenDir = "$buildDir/montiarc/foo"
  val expectedJavaGenDir = "$expectedGenDir/java"
  val expectedSymbolGenDir = "$expectedGenDir/symbols"

  mandatoryFiles.from(
    "$expectedJavaGenDir/foopackage/Foo.java",
    "$expectedJavaGenDir/barpackage/BarTOP.java",
    "$expectedSymbolGenDir/foopackage/Foo.arcsym",
    "$expectedSymbolGenDir/barpackage/Bar.arcsym"
  )
}
tasks.check.configure { dependsOn(checkGenerationTask) }



abstract class CheckFilesArePresent : DefaultTask() {

  @get:InputFiles
  abstract val mandatoryFiles: ConfigurableFileCollection

  @TaskAction
  fun execute() {
    val absentFiles = mandatoryFiles.files.filter { !it.exists() }
    if (absentFiles.isNotEmpty()) {
      val errorMsg = absentFiles.joinToString(separator = "\n") { "Missing expected file: ${it.path}" }

      throw VerificationException(errorMsg)
    }
  }
}
