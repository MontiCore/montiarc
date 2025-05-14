/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("cd2pojo")
  id("montiarc-jsim")
}

sourceSets {
  main {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/main/cd2pojo"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
    }
  }
  test {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/test/cd2pojo"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/test/montiarc"))
    }
  }
}

// The MontiArc plugin always adds montiarc-base as a dependency to the montiarc configuration.
// We want to avoid this for the montiarc-base project itself.
// Therefore, we add an exclusion rule.
configurations.montiarc.get()
  .exclude("montiarc.libraries", "montiarc-base")
configurations.montiarc.get()
  .exclude("montiarc.libraries", "maunit")

dependencies {
  testImplementation(libs.guava)
  testImplementation(libs.janino)
  testImplementation(project(":libraries:simulator-test-rte"))
}

cd2pojo {
  internalMontiArcTesting.set(true)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)
}
