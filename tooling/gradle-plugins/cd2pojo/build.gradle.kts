/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

gradlePlugin {
  plugins {
    create("Cd2pojo") {
      id = "cd2pojo"
      implementationClass = "montiarc.gradle.cd2pojo.Cd2PojoPlugin"
    }
  }
}
