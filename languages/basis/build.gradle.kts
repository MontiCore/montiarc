/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
  id("montiarc.build.java-test-fixtures")
}

layout.buildDirectory.set(project(":languages").layout.buildDirectory.dir("${project.name}"))

dependencies {
  grammar(seLibs.mc.grammar)

  api(seLibs.mc.grammar)
  api(seLibs.se.commons.logging)

  implementation(libs.apache.commons)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(libs.mockito)

  testFixturesImplementation(libs.guava)
  testFixturesImplementation(libs.janino)
  testFixturesImplementation(libs.apache.commons)
  testFixturesImplementation(libs.junit.api)
  testFixturesImplementation(libs.assertj)
  testFixturesApi(libs.junit.params)
}
