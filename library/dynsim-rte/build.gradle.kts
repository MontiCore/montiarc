/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.6.0"
  id("montiarc.build.java-library")
}

dependencies {
  api(project(":language:mode-transitions"))
  implementation("${libs.kotlinCoroutines}:${libs.kotlinxVersion}")
  implementation("${libs.kotlinStdlib}:${libs.kotlinVersion}")
  testImplementation("${libs.kotlinJunit}:${libs.kotlinVersion}")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

kotlin.sourceSets["main"].kotlin.srcDirs(setOf("main/kotlin"))
kotlin.sourceSets["main"].resources.srcDirs(setOf("main/resources"))
kotlin.sourceSets["test"].kotlin.srcDirs(setOf("test/kotlin"))
kotlin.sourceSets["test"].resources.srcDirs(setOf("test/resources"))