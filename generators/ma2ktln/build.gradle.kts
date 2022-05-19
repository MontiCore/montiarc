/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("com.github.johnrengelman.shadow")
  id("montiarc.build.java-library")
}

group = "montiarc.generators"

tasks.test {
  useJUnitPlatform()
}

dependencies {
  api(project(":language:montiarc"))

  implementation(project(":generators:ma2java"))
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

// All in one cli-jar
tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "montiarc.kotlin.generator.ModeArcTool"
  }
  isZip64 = true
  archiveClassifier.set("cli")
  archiveBaseName.set("MontiArc2Kotlin")
  archiveFileName.set("${archiveBaseName.get()}CLI-${archiveVersion.get()}.${archiveExtension.get()}")
}

tasks.jar { dependsOn(tasks.shadowJar) }