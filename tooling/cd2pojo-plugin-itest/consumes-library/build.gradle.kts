/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("cd2pojo")
}

group = "montiarc.tooling.cd2pojo-plugin-itest"

cd2pojo {
  internalMontiArcTesting.set(true)
}

dependencies {
  cd2pojo(project(":tooling:cd2pojo-plugin-itest:produces-library"))

  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
