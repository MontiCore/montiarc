/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.BuildConstants

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("montiarc-jsim")
  // This project tests MontiArc plugin application without cd2pojo
}

group = "montiarc.tooling.ma2jsim-plugin-itest.cd4montiarc-dependencies"
version = BuildConstants.VERSION

dependencies {
  cd2pojo4montiarc(project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:cd-b"))
}

tasks.getByName<Test>("test") {
  enabled = false
}

tasks.check.configure { dependsOn(tasks.compileJava) }
