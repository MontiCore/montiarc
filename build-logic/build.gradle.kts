/* (c) https://github.com/MontiCore/monticore */

plugins {
  `kotlin-dsl`
  `maven-publish`
}

group = "montiarc.build"

val repo: String = "https://nexus.se.rwth-aachen.de/content/groups/public/"

dependencies {
  implementation(libs.shadow)
  implementation(libs.spotless)
  implementation(libs.kotlin)
  implementation(libs.se.commons)
  implementation(libs.mc.generator)
  implementation(libs.mc.language.server)
  implementation(libs.node.gradle)
}

publishing {
  repositories {
    maven {
      url = uri(layout.buildDirectory.dir("repo"))
    }
  }
}

repositories {
  if(("true").equals(System.getProperty("useLocalRepo"))){
    mavenLocal()
  }
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
