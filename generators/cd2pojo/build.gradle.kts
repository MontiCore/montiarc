/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api("${libs.monticoreCD4Analysis}:${libs.monticoreVersion}")
  implementation("${libs.monticoreRuntime}:${libs.monticoreVersion}")
  implementation("${libs.monticoreGrammar}:${libs.monticoreVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.monticoreClass2MC}:${libs.monticoreVersion}")
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