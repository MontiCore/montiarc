/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(libs.se.logging)

  testImplementation(libs.mockito)
}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("simulator-rte")
  isZip64 = true
}
