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
  grammar ("${libs.monticoreOCL}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.oclGrammarsCapability) }
  }

  api(project(":languages:syscl-basis"))

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  implementation("${libs.apache}:${libs.apacheCommonsVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("${libs.monticoreOCL}:${libs.monticoreVersion}")

  testImplementation("${libs.assertj}:${libs.assertjVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

configureMCTask("SysCL.mc4")

tasks.getByName<Test>("test").useJUnitPlatform()

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}