/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("montiarc.build.java-library")
}

kotlin {
  sourceSets {
    main {
      kotlin.setSrcDirs(setOf("main/kotlin"))
      resources.setSrcDirs(setOf("main/resources"))
    }
    test {
      kotlin.setSrcDirs(setOf("test/kotlin"))
      resources.setSrcDirs(setOf("test/resources"))
    }
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
  }
}
