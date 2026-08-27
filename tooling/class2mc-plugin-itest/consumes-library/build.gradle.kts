/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("montiarc-jsim")
  id("class2mc")
}

group = "montiarc.tooling.class2mc-plugin-itest"

dependencies {
  // Only declared via the class2mc bucket, never as a montiarc/cd2pojo model dependency: this proves that
  // ExternalType is resolved through the symbol path wired by the class2mc plugin.
  class2mc(project(":tooling:class2mc-plugin-itest:produces-library"))
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
