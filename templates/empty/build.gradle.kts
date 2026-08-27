/* (c) https://github.com/MontiCore/monticore */
plugins {
  java
  application
  id("montiarc-jsim") version "7.10.0-SNAPSHOT"
  id("cd2pojo") version "7.10.0-SNAPSHOT"
  id("class2mc") version "7.10.0-SNAPSHOT"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

application {
  if (hasProperty("mainClass")) {
    mainClass.set("${property("mainClass")}")
  }
}

repositories {
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}

