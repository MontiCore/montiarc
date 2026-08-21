/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc-jsim")
  id("cd2pojo")
}

dependencies {
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
  implementation(libs.guava)
  implementation(libs.janino)
  testImplementation(libs.mockito)
  testImplementation(libs.mockito.junit)

  implementation(libs.junit.engine)
  testImplementation(libs.junit.platform.testkit)
}

// The MontiArc plugin always adds maunit as a dependency to the montiarc configuration.
// We want to avoid this for the maunit project itself.
// Therefore, we add an exclusion rule.
configurations.named("montiarc") {
  exclude(group = "montiarc.libraries", module = "maunit")
}

sourceSets {
  main {
    cd2pojo.srcDir("$projectDir/main/cd2pojo")
    montiarc.srcDir("$projectDir/main/montiarc")
  }
  test {
    cd2pojo.srcDir("$projectDir/test/cd2pojo")
    montiarc.srcDir("$projectDir/test/montiarc")
  }
}

tasks.compileCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)
}

tasks.compileTestMontiarc {
  symbolpath.from(tasks.compileCd2pojo.get().symbolOutputDir())
  useClass2Mc.set(true)
}

tasks.compileMontiarc { dependsOn(tasks.compileCd2pojo) }
