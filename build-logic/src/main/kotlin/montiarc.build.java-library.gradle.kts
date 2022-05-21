/* (c) https://github.com/MontiCore/monticore */

plugins {
  `java-library`
  id("montiarc.build.modules")
  id("montiarc.build.publishing")
  id("montiarc.build.repositories")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

java.sourceSets["main"].java.srcDirs(setOf("main/java"))
java.sourceSets["main"].resources.srcDirs(setOf("main/resources"))
java.sourceSets["test"].java.srcDirs(setOf("test/java"))
java.sourceSets["test"].resources.srcDirs(setOf("test/resources"))