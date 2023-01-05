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

val checkGenerationTask = tasks.register("checkCorrectGeneration") {
  dependsOn(tasks.named("compileFooMontiarc"))
  group = "verification"

  val expectedFooLocation = file("$buildDir/montiarc/foo/foopackage/Foo.java")
  val expectedBarLocation = file("$buildDir/montiarc/foo/barpackage/BarTOP.java")

  doLast {
    var failMessage = ""

    if (!expectedFooLocation.exists()) {
      failMessage += "Missing generation of 'Foo' at ${expectedFooLocation.path}"
    }

    if (!expectedBarLocation.exists()) {
      failMessage += if (failMessage.isEmpty()) {""} else {"\n"}  // Maybe insert line break
      failMessage += "Missing generation of 'BarTOP' at ${expectedBarLocation.path}"
    }

    if(!failMessage.isEmpty()) {
      throw VerificationException(failMessage)
    }
  }
}
tasks.named("check").configure { dependsOn(checkGenerationTask) }
