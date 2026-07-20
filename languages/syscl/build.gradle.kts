/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

layout.buildDirectory.set(project(":languages").layout.buildDirectory.dir("${project.name}"))

dependencies {
  grammar(seLibs.mc.grammar)
  grammar(project(":languages:ag"))
  grammar(project(":languages:prepost"))
  grammar(project(":languages:syscl-basis"))

  testImplementation(testFixtures(project(":languages:basis")))

  implementation(libs.apache.commons)
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(seLibs.mc.ocl)
}
