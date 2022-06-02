/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {

  api(project(":languages:core"))
  api(project(":languages:modes"))
  implementation("${libs.monticoreClass2MC}:${libs.monticoreVersion}")
  implementation("${libs.apacheBcel}:${libs.apacheBcelVersion}")

  //Other dependencies
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}