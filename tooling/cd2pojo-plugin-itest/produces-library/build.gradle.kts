/* (c) https://github.com/MontiCore/monticore */
buildscript {
  dependencies {
    classpath("montiarc.tooling:cd2pojo-plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("cd2pojo")
}

dependencies {
  implementation(libs.se.logging)
  implementation(libs.se.utilities)
}

group = "montiarc.tooling.cd2pojo-plugin-itest"

cd2pojo {
  internalMontiArcTesting.set(true)
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
