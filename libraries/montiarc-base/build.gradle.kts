/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc")
}

val hwcDir = "$projectDir/main/java"

sourceSets {
  main {
    montiarc.srcDir("$projectDir/main/montiarc")
  }
}

// The MontiArc plugin always adds montiarc-base as a dependency to the montiarc configuration.
// This makes no sense for the montiarc-base project itself. Therefore, we add an exclude rule.
configurations.montiarc.get()
  .exclude("montiarc.libraries", "montiarc-base")

dependencies {
  testImplementation(libs.guava)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}
