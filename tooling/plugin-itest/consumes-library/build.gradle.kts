/* (c) https://github.com/MontiCore/monticore */
buildscript {
  dependencies {
    classpath("montiarc.tooling:plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc")
}

group = "montiarc.tooling.plugin-itest"

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  montiarc(project(":tooling:plugin-itest:produces-library"))
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
