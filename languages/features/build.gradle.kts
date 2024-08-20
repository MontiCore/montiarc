/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(libs.mc.grammar)
  grammar(libs.mc.sc)
  grammar(project(":languages:basis"))

  api(project(":languages:core"))

  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.z3)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(libs.mockito)
}

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}