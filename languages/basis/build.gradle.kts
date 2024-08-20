/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(libs.mc.grammar)

  api(libs.mc.grammar)
  api(libs.se.logging)

  implementation(libs.apache)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(libs.mockito)
}

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}