/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("java-gradle-plugin")

  // TODO: replace the kotlin and montiarc.* plugins by applying the montiarc.build.kotlin-library plugin.
  //  However this does not work at the moment, as applying the kotlin-library plugin will lead to empty source sets...
  // id("montiarc.build.kotlin-library")

  kotlin("jvm") version "1.5.31"

  id("montiarc.build.modules")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")
  id("montiarc.build.publish-java")
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