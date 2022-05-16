/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.publishing")
}

buildDir = project(":language").buildDir

val grammars_classifier: String by project

tasks.register<Jar>("grammarJar") {
  from("$projectDir/grammars" )
  include("*.mc4")
  destinationDirectory.set(file("$buildDir/libs"))
  archiveBaseName.set(project.name)
  archiveClassifier.set(grammars_classifier)
  archiveVersion.set(project.version.toString())
}