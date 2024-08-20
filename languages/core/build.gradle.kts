/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(libs.mc.grammar)
  grammar(libs.mc.sc)
  grammar(project(":languages:automaton"))
  grammar(project(":languages:comfy"))
  grammar(project(":languages:generics"))

  api(project(":languages:automaton"))
  api(project(":languages:comfy"))
  api(project(":languages:generics"))

  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(libs.mockito)
}
