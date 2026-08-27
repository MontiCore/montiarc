/* (c) https://github.com/MontiCore/monticore */
plugins {
  java
  application
  id("montiarc-jsim") version "7.10.0-SNAPSHOT"
  id("cd2pojo") version "7.10.0-SNAPSHOT"
}

application {
  if (hasProperty("mainClass")) {
    mainClass.set("${property("mainClass")}")
  }
}

repositories {
  mavenLocal()
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}

tasks.compileMontiarc {
  //checkVariability.set(true)
}
