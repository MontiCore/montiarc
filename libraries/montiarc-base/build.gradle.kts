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

// Configurations
val generateCD = configurations.create("generateCD")

dependencies {
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.se.logging)
  implementation(libs.se.utilities)
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

// Setting up task dependencies
tasks.compileJava { dependsOn(tasks.compileMontiarc) }
