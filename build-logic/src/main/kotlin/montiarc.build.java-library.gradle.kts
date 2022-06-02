/* (c) https://github.com/MontiCore/monticore */

plugins {
  `java-library`
  id("montiarc.build.jacoco")
  id("montiarc.build.modules")
  id("montiarc.build.publish-java")
  id("montiarc.build.repositories")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

java.sourceSets["main"].java.setSrcDirs(setOf("main/java"))
java.sourceSets["main"].resources.setSrcDirs(setOf("main/resources"))
java.sourceSets["test"].java.setSrcDirs(setOf("test/java"))
java.sourceSets["test"].resources.setSrcDirs(setOf("test/resources"))

tasks.test.get().systemProperty("buildDir", "$buildDir")