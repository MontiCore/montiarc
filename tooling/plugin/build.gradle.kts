/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("java-gradle-plugin")

  id("montiarc.build.kotlin-library")
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