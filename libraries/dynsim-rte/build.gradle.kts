/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.6.0"
  id("montiarc.build.publishing")
  id("montiarc.build.repositories")
}

val kotlinx_version: String by project
val kotlin_version: String by project

dependencies {
  // Kotlin dependencies
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_version")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/kotlin"))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.srcDirs(setOf("test/kotlin"))
    resources.setSrcDirs(setOf("test/models"))
  }
}
