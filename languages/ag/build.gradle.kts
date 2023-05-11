/* (c) https://github.com/MontiCore/monticore */

import montiarc.build.Language.Companion.configureMCTask

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

configurations {
  grammar {
    isCanBeResolved=true
    isCanBeConsumed=true
  }
}

dependencies {
  grammar(libs.mc.grammar) {
    capabilities { requireCapability("de.monticore:monticore-grammar-grammars") }
  }

  api(project(":languages:basis"))

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  implementation(libs.apache)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(libs.mockito)
}

configureMCTask("ArcAGs.mc4")

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}