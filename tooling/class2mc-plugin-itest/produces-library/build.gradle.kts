/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
}

group = "montiarc.tooling.class2mc-plugin-itest"

tasks.getByName<Test>("test") {
  this.enabled = false
}
