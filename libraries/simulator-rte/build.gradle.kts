/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

dependencies {
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}
