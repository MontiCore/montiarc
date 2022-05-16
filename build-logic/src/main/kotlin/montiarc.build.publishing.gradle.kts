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
  publications.create<MavenPublication>("mavenJava") {
    configureJavaModulePublication()
  }
  repositories.maven {
    name = "SE-nexus"
    url = if (version.toString().endsWith("SNAPSHOT")) uri(snapshotsRepoUrl) else uri(releasesRepoUrl)
    credentials {
      username = mavenUser
      password = mavenPassword
    }
  }
}

fun MavenPublication.configureJavaModulePublication() {
  from(components["java"])
  artifactId = project.name
  pom {
    url.set("https://github.com/MontiCore/montiarc")
    licenses {
      license {
        name.set("MontiCore-3-Level-License-Model")
        url.set("https://monticore.github.io/monticore/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL/")
      }
    }
    developers {
      developer {
        name.set("The MontiArc Team")
        organization.set("Software Engineering RWTH Aachen University")
        organizationUrl.set("https://monticore.de/")
      }
    }
  }
}