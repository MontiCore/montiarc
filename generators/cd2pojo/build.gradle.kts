/* (c) https://github.com/MontiCore/monticore */

val mc_version: String by project
val se_commons_version: String by project
val janino_version: String by project
val guava_version: String by project
val groovy_version: String by project
val cd4a_version: String by project
val junit_jupiter_version: String by project

plugins {
  java
  id("com.github.johnrengelman.shadow") // Version is declared in settings.gradle
  id("montiarc.build.publishing")
  id("montiarc.build.repositories")
}

group = "montiarc.generators"

tasks.test {
  useJUnitPlatform()
}

dependencies {
  implementation("de.monticore:monticore-runtime:$mc_version")
  implementation("de.monticore:monticore-grammar:$mc_version")
  implementation("de.monticore:class2mc:$mc_version")
  implementation("de.monticore.lang:cd4analysis:$cd4a_version")
  implementation("de.se_rwth.commons:se-commons-groovy:$se_commons_version")
  implementation("org.codehaus.janino:janino:$janino_version")
  implementation("com.google.guava:guava:$guava_version")
  implementation("com.github.javaparser:javaparser-core:3.18.0")
  implementation("org.codehaus.groovy:groovy:$groovy_version")

  testImplementation("de.monticore:monticore-runtime:$mc_version") {
    capabilities {
      requireCapability("de.monticore:monticore-runtime-tests")
    }
  }
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")
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
