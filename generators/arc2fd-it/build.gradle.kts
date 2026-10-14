/* (c) https://github.com/MontiCore/monticore */
import montiarc.gradle.ma2jsim.MontiArcCompile

plugins {
  id("montiarc.build.integration-test")
  id("montiarc-jsim")
  id("arc2fd")
}

sourceSets {
  main {
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
    }
  }
}

dependencies {
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
}

tasks.withType(MontiArcCompile::class.java) {
  checkVariability.set(true)
}
