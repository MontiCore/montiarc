/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.integration-test")
  id("cd2pojo")
}

sourceSets {
  main {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/main/cd2pojo"))
    }
  }
}

dependencies {
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
  implementation(libs.guava)
  implementation(libs.janino)
}

tasks.compileCd2pojo {
  useClass2Mc.set(true)
  templateDir.set(file("$projectDir/main/cd2pojo"))
}
