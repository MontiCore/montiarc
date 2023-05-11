/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

val genDir = "$buildDir/generated-sources"
val genResourceDir = "$buildDir/resources/main/"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets["main"].java {
  srcDir(genDir)
}

// Configurations
val generateFD = configurations.create("generateFD")

dependencies {
  generateFD(project(":generators:arc2fd"))

  implementation(project(":libraries:majava-rte"))
  implementation(libs.se.logging)
  implementation(libs.se.utilities)
}

val genFdTask = tasks.register<JavaExec>("generateFD") {
  classpath(generateFD)
  mainClass.set("montiarc.arc2fd.FDGenerator")

  args("$projectDir/main/resources/", genResourceDir)
}

// Setting up task dependencies
tasks.compileJava { dependsOn(genFdTask)}

genFdTask { mustRunAfter(project(":generators:arc2fd").tasks.withType(Test::class)) }
