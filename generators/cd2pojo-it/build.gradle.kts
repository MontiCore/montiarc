/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.integration-test")
  id("montiarc")
  id("cd2pojo")
}

sourceSets {
  main {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/main/resources"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
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

tasks.compileCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.compileMontiarc { dependsOn(tasks.compileCd2pojo) }
