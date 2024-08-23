/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc-jsim")
  id("cd2pojo")
}

sourceSets {
  main {
    montiarc.srcDir("$projectDir/main/montiarc")
  }
  test {
    montiarc.srcDir("$projectDir/test/montiarc")
  }
}

// The MontiArc plugin always adds montiarc-base as a dependency to the montiarc configuration.
// We want to avoid this for the montiarc-base project itself.
// Therefore, we add an exclusion rule.
configurations.montiarc.get()
  .exclude("montiarc.libraries", "montiarc-base")

dependencies {
  testImplementation(libs.guava)
  testImplementation(libs.janino)
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
