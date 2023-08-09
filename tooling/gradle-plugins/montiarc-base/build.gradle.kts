/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

dependencies {
  api(project(":cd4ma-dependencies"))
  api(project(":montiarc-sources"))
  api(project(":montiarc-dependencies"))
}

gradlePlugin {
  plugins {
    create("MontiarcBase") {
      id = "montiarc-base"
      implementationClass = "montiarc.gradle.montiarc.MontiarcBasePlugin"
    }
  }
}
