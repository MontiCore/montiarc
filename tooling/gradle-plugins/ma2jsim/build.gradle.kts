/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

dependencies {
  implementation(project(":cd2pojo"))
  implementation(project(":fmu2arc"))
  implementation(project(":montiarc-base"))
}

gradlePlugin {
  plugins {
    create("MontiArc") {
      id = "montiarc-jsim"
      implementationClass = "montiarc.gradle.ma2jsim.MA2JSimPlugin"
    }
  }
}
