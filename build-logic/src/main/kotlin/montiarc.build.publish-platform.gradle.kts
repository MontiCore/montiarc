/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.Publish.Companion.configurePublication

plugins {
  id("montiarc.build.publish-base")
}

configure<PublishingExtension> {
  publications.create<MavenPublication>("platform") {
    configurePlatformPublication()
  }
}

fun MavenPublication.configurePlatformPublication() {
  from(components["javaPlatform"])
  configurePublication(project)
}