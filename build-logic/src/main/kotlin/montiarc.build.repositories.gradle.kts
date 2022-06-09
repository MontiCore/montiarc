/* (c) https://github.com/MontiCore/monticore */

val repo: String = "https://nexus.se.rwth-aachen.de/content/groups/public/"

repositories {
  mavenLocal()
  maven {
    url = uri(repo)
  }
  mavenCentral()
}