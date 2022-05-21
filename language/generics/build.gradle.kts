/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":language").buildDir.toString() + "/${project.name}")

dependencies {
  grammar("${libs.monticoreGrammar}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.mcGrammarsCapability) }
  }

  api(project(":language:basis"))

  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation((project(":language:basis"))) {
    capabilities {
      requireCapability("montiarc.language:basis-tests")
    }
  }

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

tasks.getByName<de.monticore.MCTask>("grammar").grammar
    .set(file(project(":language").projectDir.toString() + "/grammars/GenericArc.mc4") )

java.sourceSets["main"].java.srcDirs(tasks.getByName<de.monticore.MCTask>("grammar").outputDir)