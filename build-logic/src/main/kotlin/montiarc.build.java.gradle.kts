/* (c) https://github.com/MontiCore/monticore */
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  java
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.spotless")
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/java"))
    java.exclude("*.mc4")
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.setSrcDirs(setOf("test/java"))
    java.exclude("*.mc4")
    resources.setSrcDirs(setOf("test/resources"))
  }
}

tasks.test {
  useJUnitPlatform()
  systemProperty("buildDir", layout.buildDirectory.get().asFile.absolutePath)
}

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

dependencies {
  testImplementation(libs.assertj.get())
  testImplementation(libs.junit.api.get())
  testImplementation(libs.junit.params.get())
  testImplementation(libs.junit.pioneer.get())
  testRuntimeOnly(libs.junit.engine.get())
  testRuntimeOnly(libs.junit.platform.launcher.get())
}
