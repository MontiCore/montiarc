/* (c) https://github.com/MontiCore/monticore */

val shadow_version: String by project

plugins {
  id("java")
  id("com.github.johnrengelman.shadow") version "6.0.0"
  id("montiarc.build.publishing")
}

val se_commons_version: String by project
val mockito_version: String by project
val junit_jupiter_version: String by project

dependencies {
  implementation("de.se_rwth.commons:se-commons-logging:$se_commons_version")
  testImplementation("org.mockito:mockito-core:$mockito_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/java"))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.srcDirs(setOf("test/java"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}

tasks {
  named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    minimize()
    archiveBaseName.set("maJava-rte")
    isZip64 = true
  }
}

tasks.getByName<Test>("test").useJUnitPlatform()