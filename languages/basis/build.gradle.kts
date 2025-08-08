/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

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
