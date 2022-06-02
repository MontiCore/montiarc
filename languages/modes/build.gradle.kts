/* (c) https://github.com/MontiCore/monticore */

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

  implementation("${libs.apache}:${libs.apacheVersion}")
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

tasks.getByName<de.monticore.MCTask>("grammar").grammar
    .set(file(project(":languages").projectDir.toString() + "/grammars/BasicModeAutomata.mc4") )

java.sourceSets["main"].java.srcDirs(tasks.getByName<de.monticore.MCTask>("grammar").outputDir)