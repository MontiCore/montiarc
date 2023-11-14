/* (c) https://github.com/MontiCore/monticore */
import java.lang.System.getProperty

val repo: String = "https://nexus.se.rwth-aachen.de/content/groups/public/"

repositories {
  if ("true" == getProperty("useLocalRepo")) {
    mavenLocal()
  }
  maven {
    url = uri(repo)
  }
  mavenCentral()
}
