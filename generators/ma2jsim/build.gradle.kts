/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.build-info")
  id("montiarc.build.java-library")
}

dependencies {
  api(project(":languages:montiarc"))
  implementation(project(":libraries:simulator-rte"))
  implementation(libs.freemarker)
  implementation(libs.format)
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(seLibs.mc.ocl)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(project(":generators:cd2pojo"))
  testImplementation(libs.mockito)

  // Provide API only for montiarc.generator.MA2JSimTest#testRun()
  testRuntimeOnly(libs.api.guardian)
}
