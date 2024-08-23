/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc-jsim")
}

group = "montiarc.tooling.ma2java-plugin-itest"

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  montiarc(project(":tooling:ma2java-plugin-itest:produces-library"))
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
