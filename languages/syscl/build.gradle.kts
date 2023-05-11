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
  grammar (libs.mc.ocl) {
    capabilities { requireCapability("de.monticore.lang:ocl-grammars") }
  }

  api(project(":languages:generics"))
  api(project(":languages:syscl-basis"))
  api(project(":languages:ag"))
  api(project(":languages:prepost"))

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

configureMCTask("SysCL.mc4")

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}