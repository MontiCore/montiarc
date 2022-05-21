/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.kotlin-library")
}

dependencies {
  api(project(":language:mode-transitions"))
  implementation("${libs.kotlinCoroutines}:${libs.kotlinxVersion}")
  implementation("${libs.kotlinStdlib}:${libs.kotlinVersion}")
  testImplementation("${libs.kotlinJunit}:${libs.kotlinVersion}")
}

java {
  //withJavadocJar()
  withSourcesJar()
}