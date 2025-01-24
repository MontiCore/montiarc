/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.BuildConstants

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("cd2pojo")
  id("montiarc-jsim")
}

group = "montiarc.tooling.ma2jsim-plugin-itest.cd4montiarc-dependencies"
version = BuildConstants.VERSION

cd2pojo {
  internalMontiArcTesting.set(true)
}

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  cd2pojo(project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:cd-b"))
}

tasks.getByName<Test>("test") {
  enabled = false
}

tasks.check.configure { dependsOn(tasks.compileJava) }
