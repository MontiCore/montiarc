/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

dependencies {
  implementation(project(":montiarc-base"))
}

gradlePlugin {
  plugins {
    create("Arc2FD") {
      id = "arc2fd"
      implementationClass = "montiarc.gradle.arc2fd.Arc2FDPlugin"
    }
  }
}
