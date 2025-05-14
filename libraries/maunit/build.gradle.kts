/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
  id("montiarc-jsim")
  id("cd2pojo")
}

dependencies {
  implementation(libs.se.logging)
  implementation(libs.se.utilities)
  implementation(libs.guava)
  implementation(libs.janino)
  testImplementation(libs.mockito)
  testImplementation(libs.mockito.junit)

  implementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
  testImplementation("org.junit.platform:junit-platform-testkit:1.10.2")
}

// The MontiArc plugin always adds maunit as a dependency to the montiarc configuration.
// We want to avoid this for the maunit project itself.
// Therefore, we add an exclusion rule.
configurations.montiarc.get()
  .exclude("montiarc.libraries", "maunit")

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
}

tasks.compileTestMontiarc {
  symbolImportDir.from(tasks.compileCd2pojo.get().symbolOutputDir())
  useClass2Mc.set(true)
}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("maunit")
  isZip64 = true
}

tasks.compileMontiarc { dependsOn(tasks.compileCd2pojo) }
