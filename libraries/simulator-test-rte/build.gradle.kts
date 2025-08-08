/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

dependencies {
  implementation(project(":libraries:simulator-rte"))
  api(seLibs.se.commons.logging)
  api(libs.junit.api)
}
