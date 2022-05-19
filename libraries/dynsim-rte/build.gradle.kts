/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.6.0"
  id("montiarc.build.java-library")
}

dependencies {
  implementation("${libs.kotlinCoroutines}:${libs.kotlinxVersion}")
  implementation("${libs.kotlinStdlib}:${libs.kotlinVersion}")

  testImplementation("${libs.kotlinJunit}:${libs.kotlinVersion}")
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
