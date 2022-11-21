/* (c) https://github.com/MontiCore/monticore */

plugins {
  `kotlin-dsl`
  `maven-publish`
}

group = "montiarc.build"

val repo: String = "https://nexus.se.rwth-aachen.de/content/groups/public/"

dependencies {
  implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
  implementation("monticore:monticore.gradle.plugin:7.5.0-SNAPSHOT")
  implementation("de.monticore.language-server:de.monticore.language-server.gradle.plugin:7.5.0-SNAPSHOT")
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