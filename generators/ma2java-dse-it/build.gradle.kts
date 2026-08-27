/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.integration-test")
  id("cd2pojo")
  id("montiarc")
  application
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
  implementation(project(":libraries:majava-dse-rte"))
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.apache.commons)
  testImplementation(libs.apache.poi)
}

tasks.compileMontiarc {
  dse.set(true)
}

application {
  mainClass.set("main.MainDse")
}
