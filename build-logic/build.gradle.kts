/* (c) https://github.com/MontiCore/monticore */

plugins {
  `kotlin-dsl`
  `maven-publish`
}

group = "montiarc.build"

publishing {
  repositories {
    maven {
      url = uri(layout.buildDirectory.dir("repo"))
    }
  }
}

repositories {
  gradlePluginPortal()
}

tasks.withType<JavaCompile> {
  sourceCompatibility = JavaVersion.VERSION_11.toString()
  targetCompatibility = JavaVersion.VERSION_11.toString()
  options.encoding = "UTF-8"
  options.isFork = false
  options.isDeprecation = true
  options.isWarnings = true
}