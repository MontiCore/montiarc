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

  api(project(":languages:core"))
  api(project(":languages:compute"))
  api(project(":languages:features"))
  api(project(":languages:generics"))
  api(project(":languages:modes"))

  implementation(libs.mc.c2mc)
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.z3)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(libs.mockito)
}

configureMCTask("MontiArc.mc4")
