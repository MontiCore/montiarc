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

dependencies {
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
  implementation(libs.guava)
  implementation(libs.janino)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)
  dependsOn(tasks.compileFmu2arc)
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)
  dependsOn(tasks.compileTestFmu2arc)
}

// Don't run tests for certain architectures because fmus don't contain correct binaries
tasks.withType<Test>().configureEach {
  onlyIf {
    System.getProperty("os.arch") != "aarch64"
  }
}
