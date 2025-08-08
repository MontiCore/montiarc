/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(seLibs.mc.grammar)
  grammar(seLibs.mc.statecharts)
  grammar(project(":languages:basis"))

  api(project(":languages:basis"))
  api(seLibs.mc.statecharts) {
    exclude("org.apache.groovy", "groovy")
  }

  implementation(libs.apache.commons)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(libs.mockito)
}
