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

dependencies {
  implementation(libs.se.logging)
  implementation(libs.se.utilities)
  implementation(libs.guava)
  implementation(libs.janino)
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

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.compileTestCd2pojo {
  useClass2Mc.set(true)

  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
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
