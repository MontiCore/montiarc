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

group = "montiarc.tooling.cd2pojo-plugin-itest"

cd2pojo {
  internalMontiArcTesting.set(true)
}

dependencies {
  cd2pojo(project(":tooling:cd2pojo-plugin-itest:produces-library"))

  implementation(libs.se.logging)
  implementation(libs.se.utilities)
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
