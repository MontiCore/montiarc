/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.Publish.Companion.configurePublication

plugins {
  id("montiarc.build.publish-base")
}

buildDir = project(":languages").buildDir

val grammarTask : Jar by tasks.register<Jar>("grammarJar") {
  from("$projectDir/grammars")
  include("*.mc4")
  destinationDirectory.set(file("$buildDir/libs"))
  archiveBaseName.set(project.name)
  archiveClassifier.set("grammars")
  archiveVersion.set(project.version.toString())
}

val grammars: Configuration by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = false
}

val grammarArtifact = artifacts.add("grammars", grammarTask)

configure<PublishingExtension> {
  publications.create<MavenPublication>("grammars") {
    artifact(grammarArtifact)
    configurePublication(project)
  }
}