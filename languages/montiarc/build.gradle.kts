/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjectionForUpToDateChecks

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

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

// Inject generator version information into java code for up to date checks
registerVersionInjectionForUpToDateChecks(
  taskName = "injectGeneratorVersion",
  genDir = "${project.buildDir}/generated-resources/main",
  subfolder = "montiarc",
  fileName = "MontiArcToolVersion.txt",
)
