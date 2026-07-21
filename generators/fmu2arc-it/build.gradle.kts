/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.integration-test")
  id("montiarc-jsim")
  id("fmu2arc")
}

sourceSets {
  main {
    fmu2arc {
      setSrcDirs(setOf("$projectDir/main/fmu2arc"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
    }
  }
  test {
    fmu2arc {
      setSrcDirs(setOf("$projectDir/test/fmu2arc"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/test/montiarc"))
    }
  }
}

tasks.compileMontiarc {
  useClass2Mc.set(true)
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)
}

// Don't run tests for certain architectures because fmus don't contain correct binaries
tasks.withType<Test>().configureEach {
  onlyIf {
    System.getProperty("os.arch") != "aarch64"
  }
}
