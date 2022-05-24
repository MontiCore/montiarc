/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.Publish.Companion.configurePublication

plugins {
  id("montiarc.build.publish-base")
}

configure<PublishingExtension> {
  publications.create<MavenPublication>("java") {
    configureJavaPublication()
  }
}

fun MavenPublication.configureJavaPublication() {
  from(components["java"])
  configurePublication(project)
}