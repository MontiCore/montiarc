/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc-jsim")
}

group = "montiarc.tooling.ma2jsim-plugin-itest"

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  montiarc(project(":tooling:ma2jsim-plugin-itest:consumes-library"))

  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
