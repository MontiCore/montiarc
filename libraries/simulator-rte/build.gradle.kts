/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
  id("cd2pojo")
}

sourceSets {
  main {
    cd2pojo.srcDir("$projectDir/main/cd2pojo")
  }
  test {
    cd2pojo.srcDir("$projectDir/main/cd2pojo")
  }
}

dependencies {
  implementation(libs.se.logging)

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
