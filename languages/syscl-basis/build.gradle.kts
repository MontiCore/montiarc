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
  grammar("${libs.monticoreGrammar}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.mcGrammarsCapability) }
  }

  api(project(":languages:basis"))

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  implementation("${libs.apache}:${libs.apacheCommonsVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}

configureMCTask("SysCLBasis.mc4")

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}