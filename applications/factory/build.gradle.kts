/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("cd2pojo")
  id("montiarc-jsim")
  id("montiarc.build.integration-test")
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

tasks.compileCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileTestCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)

  debugTask.set(false)
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)

  debugTask.set(false)
}
