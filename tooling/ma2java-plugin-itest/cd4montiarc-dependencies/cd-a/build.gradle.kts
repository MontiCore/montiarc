/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("cd2pojo")
}

group = "montiarc.tooling.ma2java-plugin-itest.cd4montiarc-dependencies"
version = "7.6.0-SNAPSHOT"

cd2pojo {
  internalMontiArcTesting.set(true)
}

tasks.getByName<Test>("test") {
  enabled = false
}

tasks.check.configure { dependsOn(tasks.compileJava) }
