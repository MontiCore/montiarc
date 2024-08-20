/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

dependencies {
  grammar(libs.mc.grammar)
  grammar(libs.mc.sc)
  grammar(project(":languages:basis"))

  api(project(":languages:core"))
  api(project(":languages:features"))

  implementation(libs.apache)
  implementation(libs.guava)
  implementation(libs.janino)

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