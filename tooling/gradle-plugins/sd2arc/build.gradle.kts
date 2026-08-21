/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

dependencies {
  implementation(project(":cd2pojo"))
  implementation(project(":ma2jsim"))
  implementation(project(":ma2java"))
  implementation(project(":montiarc-sources"))
}

gradlePlugin {
  plugins {
    create("SD2Arc") {
      id = "sd2arc"
      implementationClass = "montiarc.gradle.sd2arc.SD2ArcPlugin"
    }
  }
}
