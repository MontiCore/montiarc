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

  //MontiCore dependencies
  api(project(":languages:core"))

  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("org.openjdk.nashorn:nashorn-core:15.4")

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}

configureMCTask("VariableArc.mc4")

java.sourceSets["main"].java.srcDirs(tasks.getByName<de.monticore.MCTask>("grammar").outputDir)

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}