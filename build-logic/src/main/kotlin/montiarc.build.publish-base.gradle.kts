/* (c) https://github.com/MontiCore/monticore */

plugins {
  `maven-publish`
}

val mavenUser: String?
  get() = System.getenv("user")

val mavenPassword: String?
  get() = System.getenv("pass")

val snapshotsRepoUrl: String = "https://nexus.se.rwth-aachen.de/content/repositories/montiarc-snapshots/"

val releasesRepoUrl: String = "https://nexus.se.rwth-aachen.de/content/repositories/montiarc-releases/"

configure<PublishingExtension> {
  repositories.maven {
    name = "SE-nexus"
    url = if (version.toString().endsWith("SNAPSHOT")) uri(snapshotsRepoUrl) else uri(releasesRepoUrl)
    credentials {
      username = mavenUser
      password = mavenPassword
    }
  }
}