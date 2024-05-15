import montiarc.build.BuildConstants

/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("montiarc")
  // This project tests MontiArc plugin application without cd2pojo
}

group = "montiarc.tooling.ma2java-plugin-itest.cd4montiarc-dependencies"
version = BuildConstants.VERSION

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  cd2pojo4montiarc(project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:cd-b"))
}

tasks.getByName<Test>("test") {
  enabled = false
}

tasks.check.configure { dependsOn(tasks.compileJava) }
