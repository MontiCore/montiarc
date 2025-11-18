/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

layout.buildDirectory.set(project(":languages").layout.buildDirectory.dir("${project.name}"))

dependencies {
  grammar(seLibs.mc.grammar)
  grammar(seLibs.mc.statecharts)
  grammar(project(":languages:automaton"))
  grammar(project(":languages:compute"))
  grammar(project(":languages:comfy"))
  grammar(project(":languages:features"))
  grammar(project(":languages:modes"))

  api(project(":languages:compute"))
  api(project(":languages:features"))
  api(project(":languages:modes"))

  implementation(seLibs.mc.c2mc)
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.z3)

  runtimeOnly(seLibs.mc.stream.symbols)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(libs.mockito)
}
