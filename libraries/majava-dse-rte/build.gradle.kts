/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api(project(":libraries:majava-rte"))
  api(libs.z3)

  implementation(libs.se.logging)
  implementation(libs.se.utilities)
  implementation(libs.apache)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(libs.mockito)
}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("ma2Java-symbolic-rte")
  isZip64 = true
}
