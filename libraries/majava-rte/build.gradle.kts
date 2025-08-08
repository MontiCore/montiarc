/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

dependencies {
  implementation(seLibs.se.commons.logging)

  testImplementation(libs.mockito)
}
