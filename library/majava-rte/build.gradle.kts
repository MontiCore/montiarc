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

sourceSets {
  main {
    java.setSrcDirs(setOf("main/java"))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.srcDirs(setOf("test/java"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}

tasks {
  named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    minimize()
    archiveBaseName.set("maJava-rte")
    isZip64 = true
  }
}

tasks.getByName<Test>("test").useJUnitPlatform()