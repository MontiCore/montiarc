/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

layout.buildDirectory.set(project(":languages").layout.buildDirectory.dir("${project.name}"))

dependencies {
  grammar(seLibs.mc.grammar)
  grammar(seLibs.mc.ucd)
  grammar(project(":languages:basis"))

  api(project(":languages:basis"))

  implementation(libs.apache.commons)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(testFixtures(project(":languages:basis")))

  testImplementation(libs.mockito)
}
