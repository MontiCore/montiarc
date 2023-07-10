/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("java-gradle-plugin")
  id("montiarc.build.kotlin")
  id("montiarc.build.project-version")
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
