/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

dependencies {
  implementation(project(":cd2pojo"))
  implementation(project(":montiarc-dependencies"))
}
