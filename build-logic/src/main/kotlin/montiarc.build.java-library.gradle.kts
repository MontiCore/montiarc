/* (c) https://github.com/MontiCore/monticore */

plugins {
  `java-library`
  id("montiarc.build.publishing")
  id("montiarc.build.repositories")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}