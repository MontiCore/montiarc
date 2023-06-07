/* (c) https://github.com/MontiCore/monticore */

buildscript {
  dependencies {
    classpath("montiarc.tooling:cd2pojo-plugin")
    classpath("montiarc.tooling:plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("cd2pojo")
  id("montiarc")
}

group = "montiarc.tooling.plugin-itest.cd4montiarc-dependencies"
version = "7.5.0-SNAPSHOT"

dependencies {
  cd2pojo(project(":tooling:plugin-itest:cd4montiarc-dependencies:cd-b"))
}

tasks.getByName<Test>("test") {
  enabled = false
}

tasks.check.configure { dependsOn(tasks.compileJava) }
