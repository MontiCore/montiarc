/* (c) https://github.com/MontiCore/monticore */

plugins {
  `java-library`
  id("montiarc.build.jacoco")
  id("montiarc.build.modules")
  id("montiarc.build.publish-java")
  id("montiarc.build.repositories")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

java.sourceSets["main"].java.setSrcDirs(setOf("main/java"))
java.sourceSets["main"].resources.setSrcDirs(setOf("main/resources"))
java.sourceSets["test"].java.setSrcDirs(setOf("test/java"))
java.sourceSets["test"].resources.setSrcDirs(setOf("test/resources"))

tasks.test {
  systemProperty("buildDir", layout.buildDirectory.get().toString())
}