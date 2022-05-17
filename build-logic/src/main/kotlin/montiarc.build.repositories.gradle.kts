import org.gradle.kotlin.dsl.credentials

/* (c) https://github.com/MontiCore/monticore */

val mavenUser: String?
  get() = System.getenv("user")

val mavenPassword: String?
  get() = System.getenv("pass")

val repo: String = "https://nexus.se.rwth-aachen.de/content/groups/public/"

repositories {
  maven {
    credentials {
      username = mavenUser
      password = mavenPassword
    }
    url = uri(repo)
  }
  mavenCentral()
}