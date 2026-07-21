/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.build-info")
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(project(":languages:montiarc"))
  api(libs.fmi4j)
  runtimeOnly(seLibs.se.commons.logging.slf4j)
  implementation(libs.kotlin.stdlib)
  implementation(libs.format)
  implementation(libs.guava)
  implementation(libs.janino)
}

repositories {
  mavenCentral()
}
