/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(project(":languages:montiarc"))
  implementation(seLibs.mc.sd)
  implementation(seLibs.mc.runtime)
  implementation(seLibs.mc.grammar)
  implementation(libs.janino)
  implementation(libs.guava)
  implementation(seLibs.mc.c2mc)
  testImplementation(testFixtures(project(":languages:basis")))
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
