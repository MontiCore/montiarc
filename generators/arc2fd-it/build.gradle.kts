/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

val genDir = layout.buildDirectory.dir("generated-sources")
val genResourceDir = layout.buildDirectory.dir("resources/main/")

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = layout.buildDirectory.dir("logs")

sourceSets["main"].java {
  srcDir(genDir)
}

// Configurations
val generateFD = configurations.create("generateFD")

dependencies {
  generateFD(project(":generators:arc2fd"))

  implementation(project(":libraries:majava-rte"))
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
}

val genFdTask = tasks.register<JavaExec>("generateFD") {
  classpath(generateFD)
  mainClass.set("montiarc.arc2fd.FDGenerator")

  val inputDir = file("$projectDir/main/resources/")
  val outputDir = file(genResourceDir)

  // Specify input and output for Gradle's up-to-date checks
  inputs.dir(inputDir)
  outputs.dir(outputDir)

  args(inputDir, outputDir)
}

// Setting up task dependencies
tasks.compileJava { dependsOn(genFdTask)}

genFdTask { mustRunAfter(project(":generators:arc2fd").tasks.withType(Test::class)) }
