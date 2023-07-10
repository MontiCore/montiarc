/* (c) https://github.com/MontiCore/monticore */

plugins {
  kotlin("jvm")
  id("montiarc.build.jvm")
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
