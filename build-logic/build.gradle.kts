/* (c) https://github.com/MontiCore/monticore */

plugins {
  `kotlin-dsl`
  `maven-publish`
}

group = "montiarc.build"

val repo: String = "https://nexus.se.rwth-aachen.de/content/groups/public/"

dependencies {
  implementation("monticore:monticore.gradle.plugin:7.3.1")
}

publishing {
  repositories {
    maven {
      url = uri(layout.buildDirectory.dir("repo"))
    }
  }
}

repositories {
  maven {
    url = uri(repo)
  }
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