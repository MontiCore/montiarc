/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.integration-test")
  id("montiarc-jsim")
  id("cd2pojo")
  id("sd2arc")
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
    java {
      setSrcDirs(setOf("$projectDir/test/java"))
    }
    sd2arc {
      setSrcDirs(setOf("$projectDir/test/sd2arc"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/test/montiarc"))
    }
  }
}

dependencies {
  implementation(libs.se.logging)
  implementation(libs.se.utilities)
  implementation(libs.guava)
  implementation(libs.janino)
  testCd2pojo(project(":libraries:simulator-rte"))
}

sd2arc {
  internalMontiArcTesting.set(true)
}

cd2pojo {
  internalMontiArcTesting.set(true)
}

montiarc {
  internalMontiArcTesting.set(true)
}

val enableAttachDebugger = false

tasks.compileCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.compileTestSd2arc {

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}
