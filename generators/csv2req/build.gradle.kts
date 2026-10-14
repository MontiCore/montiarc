/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(project(":languages:basis"))
  implementation(seLibs.mc.grammar)
  implementation(seLibs.mc.runtime)
  implementation(libs.commons.cli)
  implementation(libs.commons.csv)
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(seLibs.se.commons.logging)

  testImplementation(testFixtures(project(":languages:basis")))
}

tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "montiarc.csv2req.CSV2ReqTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("CSV2REQ")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}
