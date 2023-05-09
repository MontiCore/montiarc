/* (c) https://github.com/MontiCore/monticore */
buildscript {
  dependencies {
    classpath("montiarc.tooling:cd2pojo-plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.modules")
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

  implementation("${libs.seCommonsLogging}:${project.version}")
  implementation("${libs.seCommonsUtils}:${project.version}")
}

tasks.getByName<Test>("test") {
  this.enabled = false
}
