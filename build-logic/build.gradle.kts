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
  implementation(libs.kotlin.gradle)
  implementation(seLibs.se.commons.gradle)
  implementation(seLibs.mc.generator)
  implementation(seLibs.mc.language.server)
  implementation(libs.node.gradle)
  implementation(libs.intellij)
  //https://github.com/gradle/gradle/issues/15383
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(files(seLibs.javaClass.superclass.protectionDomain.codeSource.location))
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
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}
