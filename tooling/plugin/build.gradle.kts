/* (c) https://github.com/MontiCore/monticore */

plugins {
  kotlin("jvm") version "1.5.31"
  id("java-gradle-plugin")
  id("montiarc.build.project-version")
  id("montiarc.build.modules")
  id("montiarc.build.repositories")
  id("montiarc.build.publish-base")
}

group = "montiarc.tooling"

gradlePlugin {
  plugins {
    create("Montiarc") {
      id = "montiarc"
      implementationClass = "montiarc.tooling.plugin.MontiarcPlugin"
    }
  }
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