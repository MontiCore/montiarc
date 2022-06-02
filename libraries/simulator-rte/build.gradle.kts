/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

dependencies {
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

tasks.getByName<Test>("test").useJUnitPlatform()