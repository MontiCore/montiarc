/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.java-test-fixtures")
  id("montiarc.build.shadow")
  id("cd2pojo")
}

sourceSets {
  main {
    cd2pojo.srcDir("$projectDir/main/cd2pojo")
  }
  test {
    cd2pojo.srcDir("$projectDir/test/cd2pojo")
  }
}

dependencies {
  api(libs.se.logging)
  api(libs.mqtt)
  implementation(libs.guava)
  implementation(libs.jackson)
  testFixturesImplementation(libs.guava)
  testFixturesImplementation(libs.mqtt)

  testImplementation(libs.mockito)
}

cd2pojo {
  internalMontiArcTesting.set(true)
}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("simulator-rte")
  isZip64 = true
}
