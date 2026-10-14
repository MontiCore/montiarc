/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

gradlePlugin {
  plugins {
    create("CSV2Req") {
      id = "csv2req"
      implementationClass = "montiarc.gradle.csv2req.CSV2ReqPlugin"
    }
  }
}
