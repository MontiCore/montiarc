/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(libs.mc.grammar)
  grammar(project(":languages:ag"))
  grammar(project(":languages:generics"))
  grammar(project(":languages:prepost"))
  grammar(project(":languages:syscl-basis"))

  api(project(":languages:ag"))
  api(project(":languages:generics"))
  api(project(":languages:prepost"))
  api(project(":languages:syscl-basis"))

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  implementation(libs.apache)
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.mc.ocl)
}

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}