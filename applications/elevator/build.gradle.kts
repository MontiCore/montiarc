/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc")
  id("cd2pojo")
}

sourceSets {
  main {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/main/cd2pojo"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
    }
  }
}

dependencies {
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
}

cd2pojo {
  internalMontiArcTesting.set(true)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileCd2pojo {
  useClass2Mc.set(true)

  debugTask.set(false)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)

  debugTask.set(false)
}
