/* (c) https://github.com/MontiCore/monticore */

plugins {
  `kotlin-dsl`
  `maven-publish`
}

group = "montiarc.build"

val repo: String = "https://nexus.se.rwth-aachen.de/content/groups/public/"

dependencies {
  implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
  implementation("com.diffplug.spotless:spotless-plugin-gradle:6.18.0")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
  implementation("monticore:monticore.gradle.plugin:7.6.0-SNAPSHOT")
  implementation("de.monticore.language-server:de.monticore.language-server.gradle.plugin:7.6.0-SNAPSHOT")
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
  options.encoding = "UTF-8"
  options.isFork = false
  options.isDeprecation = true
  options.isWarnings = true
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}
