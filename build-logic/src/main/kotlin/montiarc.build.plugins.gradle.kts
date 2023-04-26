/* (c) https://github.com/MontiCore/monticore */

plugins {
  kotlin("jvm")
  id("java-gradle-plugin")
  id("montiarc.build.project-version")
  id("montiarc.build.modules")
  id("montiarc.build.repositories")
  id("montiarc.build.publish-base")
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/kotlin"))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.setSrcDirs(setOf("test/kotlin"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}
