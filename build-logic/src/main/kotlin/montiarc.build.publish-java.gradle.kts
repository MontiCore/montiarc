/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.publish-base")
}

configure<PublishingExtension> {
  publications.create<MavenPublication>("mavenJava") {
    configureJavaModulePublication()
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