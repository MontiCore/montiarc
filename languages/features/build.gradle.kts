/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

layout.buildDirectory.set(project(":languages").layout.buildDirectory.dir("${project.name}"))

dependencies {
  grammar(seLibs.mc.grammar)
  grammar(seLibs.mc.statecharts)
  grammar(project(":languages:basis"))

  api(project(":languages:automaton"))
  api(project(":languages:compute"))

  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.z3)

  testImplementation(testFixtures(project(":languages:basis")))

  testImplementation(libs.mockito)
}
