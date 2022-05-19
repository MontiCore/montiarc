/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("com.github.johnrengelman.shadow")
  id("montiarc.build.java-library")
}

group = "montiarc.generators"

tasks.named<Test>("test") {
  useJUnitPlatform()
}

dependencies {
  api(project(":language:montiarc"))
  implementation(project(":libraries:majava-rte"))
  implementation("${libs.seCommonsGroovy}:${libs.monticoreVersion}")
  implementation("${libs.apache}:${libs.apacheVersion}")
  implementation("${libs.format}:${libs.formatVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation(project(":generators:cd2pojo"))
  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

// All in one tool-jar
tasks.shadowJar {
  minimize()
  manifest {
    attributes["Main-Class"] = "montiarc.generator.MontiArcTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("MontiArc2Java")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

tasks.jar { dependsOn(tasks.shadowJar) }