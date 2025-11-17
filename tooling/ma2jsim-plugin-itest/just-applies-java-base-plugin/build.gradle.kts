/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("montiarc-jsim")  // Implicitly applies java-base
}

group = "montiarc.tooling.ma2jsim-plugin-itest"

val fooSourceSet = sourceSets.create("foo")
val fooImplConfig = configurations.getByName(fooSourceSet.implementationConfigurationName)

val checkGenerationTask = tasks.register("checkCorrectGeneration", CheckFilesArePresent::class.java) {
  dependsOn(tasks.named("compileFooMontiarc"))
  group = "verification"

  val expectedGenDir = layout.buildDirectory.dir("montiarc/foo")
  val expectedJavaGenDir = expectedGenDir.map { it.dir("java") }
  val expectedSymbolGenDir = expectedGenDir.map { it.dir("symbols") }

  mandatoryFiles.from(
    expectedJavaGenDir.map { it.file("foopackage/FooComp.java") },
    expectedJavaGenDir.map { it.file("barpackage/BarCompImplTOP.java") },
    expectedSymbolGenDir.map { it.file("foopackage/Foo.arcsym") },
    expectedSymbolGenDir.map { it.file("barpackage/Bar.arcsym") }
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
