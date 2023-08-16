/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api(project(":languages:montiarc"))
  implementation(project(":libraries:simulator-rte"))
  implementation(libs.format)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(project(":generators:cd2pojo"))
  testImplementation(libs.mockito)
}

sourceSets["main"].java {
  srcDir("${buildDir}/montiarc/main/java")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

// All in one tool-jar
tasks.shadowJar {
  //minimize()
  manifest {
    attributes["Main-Class"] = "montiarc.generator.MontiArcTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("MontiArcSimulator")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}