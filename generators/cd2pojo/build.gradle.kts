/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api(seLibs.mc.cd4a)
  implementation(seLibs.mc.runtime)
  implementation(seLibs.mc.grammar)
  implementation(libs.janino)
  implementation(libs.guava)
  implementation(seLibs.mc.c2mc)
}

tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "de.monticore.cd2pojo.CD2PojoTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("CD2POJO")
  archiveFileName.set( "${archiveBaseName.get()}.${archiveExtension.get()}" )
}
