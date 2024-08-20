/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(libs.mc.grammar)
  grammar(libs.mc.sc)
  grammar(project(":languages:basis"))

  api(project(":languages:basis"))
  api(libs.mc.sc) {
    exclude("org.apache.groovy", "groovy")
  }

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
