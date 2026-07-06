/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

gradlePlugin {
  plugins {
    create("FMU2Arc") {
      id = "fmu2arc"
      implementationClass = "montiarc.gradle.fmu2arc.FMU2ArcPlugin"
    }
  }
}
dependencies {
  implementation(project(":montiarc-dependencies"))
}
