/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.java-test-fixtures")
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
  api(seLibs.se.commons.logging)
  api(libs.mqtt)
  api(libs.commons.cli)
  api(libs.jackson)
  implementation(libs.guava)
  testFixturesImplementation(libs.guava)
  testFixturesImplementation(libs.mqtt)

  testImplementation(libs.mockito)
}

cd2pojo {
  internalMontiArcTesting.set(true)
}
