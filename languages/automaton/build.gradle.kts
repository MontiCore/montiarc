/* (c) https://github.com/MontiCore/monticore */

import montiarc.build.Language.Companion.configureMCTask

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(libs.mc.grammar) {
    capabilities { requireCapability("de.monticore:monticore-grammar-grammars") }
  }
  grammar(libs.mc.sc) {
    capabilities { requireCapability("de.monticore.lang:statecharts-grammars") }
  }

  api(project(":languages:basis"))
  api(libs.mc.sc) {
    exclude("org.apache.groovy", "groovy")
  }

  implementation(libs.apache)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(libs.mockito)
}

configureMCTask("ArcAutomaton.mc4")
