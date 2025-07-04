/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(project(":languages:montiarc"))
  implementation(libs.mc.sd)
  implementation(libs.mc.runtime)
  implementation(libs.mc.grammar)
  implementation(libs.janino)
  implementation(libs.guava)
  implementation(libs.mc.c2mc)
}

tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "de.monticore.sd2arc.SD2ArcTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("SD2ARC")
  archiveFileName.set( "${archiveBaseName.get()}.${archiveExtension.get()}" )
}