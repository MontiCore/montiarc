/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.build-info")
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(project(":languages:montiarc"))
  api(libs.fmi4j) {
    // Exclude the old SLF4J 1.7 binding that causes the warning
    exclude(group = "org.slf4j", module = "slf4j-log4j12")
    exclude(group = "log4j", module = "log4j")
  }
  // Logback to prevent error message and user up-to-date version
  implementation(libs.slf4j.simple)
  implementation(libs.kotlin.stdlib)
  implementation(libs.format)
  implementation(libs.guava)
  implementation(libs.janino)
}

repositories {
  mavenCentral()
}
