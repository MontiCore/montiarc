/* (c) https://github.com/MontiCore/monticore */

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
  cd2pojo(project(":tooling:cd2pojo-plugin-itest:consumes-library"))

  implementation(libs.se.logging)
  implementation(libs.se.utilities)

  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
