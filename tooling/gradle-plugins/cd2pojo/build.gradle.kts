/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

gradlePlugin {
  plugins {
    create("CD2Pojo") {
      id = "cd2pojo"
      implementationClass = "montiarc.gradle.cd2pojo.CD2PojoPlugin"
    }
  }
}
