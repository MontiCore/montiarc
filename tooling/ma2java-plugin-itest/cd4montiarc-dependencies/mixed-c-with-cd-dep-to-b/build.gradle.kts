/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("cd2pojo")
  id("montiarc")
}

group = "montiarc.tooling.ma2java-plugin-itest.cd4montiarc-dependencies"
version = "7.6.0-SNAPSHOT"

dependencies {
  cd2pojo(project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:cd-b"))
}

tasks.getByName<Test>("test") {
  enabled = false
}

tasks.check.configure { dependsOn(tasks.compileJava) }
