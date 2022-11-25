/* (c) https://github.com/MontiCore/monticore */

import montiarc.build.Language.Companion.configureMCTask

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar("${libs.monticoreGrammar}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.mcGrammarsCapability) }
  }
  grammar("${libs.monticoreStatecharts}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.scGrammarsCapability) }
  }

  api(project(":languages:core"))
  api(project(":languages:compute"))
  api(project(":languages:features"))
  api(project(":languages:generics"))
  api(project(":languages:modes"))

  implementation(libs.monticoreClass2MC)
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}

configureMCTask("MontiArc.mc4")
