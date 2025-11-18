/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.build-info")
  id("montiarc.build.java-library")
}

dependencies {
  api(project(":languages:montiarc"))
  implementation(project(":libraries:majava-rte"))
  implementation(libs.format)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(project(":generators:cd2pojo"))
  testImplementation(seLibs.mc.c2mc)
  testImplementation(libs.mockito)
}
