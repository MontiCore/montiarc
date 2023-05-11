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

  //MontiCore dependencies
  api(project(":languages:core"))

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

configureMCTask("VariableArc.mc4")

java.sourceSets["main"].java.srcDirs(tasks.getByName<de.monticore.MCTask>("grammar").outputDir)

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}