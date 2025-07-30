/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

dependencies {
  api(project(":libraries:majava-rte"))
  api(libs.z3)

  implementation(libs.se.logging)
  implementation(libs.se.utilities)
  implementation(libs.apache.commons)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(libs.mockito)
}
