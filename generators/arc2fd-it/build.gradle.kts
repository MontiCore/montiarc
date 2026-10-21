/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.integration-test")
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
