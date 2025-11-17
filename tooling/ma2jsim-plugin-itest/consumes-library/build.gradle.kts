/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc-jsim")
}

group = "montiarc.tooling.ma2jsim-plugin-itest"

dependencies {
  montiarc(project(":tooling:ma2jsim-plugin-itest:produces-library"))
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
