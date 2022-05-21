/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("com.github.johnrengelman.shadow")
}

tasks.test {
  useJUnitPlatform()
}

dependencies {
  api("${libs.monticoreCD4Analysis}:${libs.monticoreVersion}")
  api("${libs.monticoreGrammar}:${libs.monticoreVersion}")
  api("${libs.monticoreRuntime}:${libs.monticoreVersion}")

  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("${libs.codehausGroovy}:${libs.codehausVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.javaParser}:${libs.javaParserVersion}")
  implementation("${libs.monticoreClass2MC}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsGroovy}:${libs.monticoreVersion}")

  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
  minimize()
  manifest {
    attributes["Main-Class"] = "de.monticore.cd2pojo.POJOGeneratorScript"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("CD2POJO")
  archiveFileName.set( "${archiveBaseName.get()}.${archiveExtension.get()}" )
}

tasks.jar { dependsOn(tasks.shadowJar) }
