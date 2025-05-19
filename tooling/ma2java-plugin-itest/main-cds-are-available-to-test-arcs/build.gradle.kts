/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("cd2pojo")
  id("montiarc")
}

group = "montiarc.tooling.ma2java-plugin-itest"

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
