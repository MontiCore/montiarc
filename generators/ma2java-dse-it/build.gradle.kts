/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.integration-test")
  id("cd2pojo")
  id("montiarc")
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
  implementation(project(":libraries:majava-dse-rte"))
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(group= "org.apache.commons", name= "commons-lang3", version= "3.12.0")
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
  dse.set(true)
  useClass2Mc.set(true)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}
