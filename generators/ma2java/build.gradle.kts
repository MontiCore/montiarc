/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api(project(":languages:montiarc")) {
    exclude("tools.aqua", "z3-turnkey")
  }
  implementation(project(":libraries:majava-rte"))
  implementation("${libs.format}:${libs.formatVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation(project(":generators:cd2pojo"))
  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
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
  archiveBaseName.set("MontiArc2Java")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}