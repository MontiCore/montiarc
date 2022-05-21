/* (c) https://github.com/MontiCore/monticore */

val shadow_version: String by project

plugins {
  id("com.github.johnrengelman.shadow") version "6.0.0"
  id("montiarc.build.java-library")
}

dependencies {
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

tasks {
  named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    minimize()
    archiveBaseName.set("maJava-rte")
    isZip64 = true
  }
}

tasks.getByName<Test>("test").useJUnitPlatform()