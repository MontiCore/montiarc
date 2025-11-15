/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
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
}

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}
