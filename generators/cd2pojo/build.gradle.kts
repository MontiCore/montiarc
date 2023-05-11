/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api(libs.mc.cd4a)
  implementation(libs.mc.runtime)
  implementation(libs.mc.grammar)
  implementation(libs.janino)
  implementation(libs.guava)
  implementation(libs.mc.c2mc)
}

java {
  //withJavadocJar()
  withSourcesJar()
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