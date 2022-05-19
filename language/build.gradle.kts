/* (c) https://github.com/MontiCore/monticore */

buildDir = project(":language").buildDir

tasks.register<Jar>("grammarJar") {
  from("$projectDir/grammars" )
  include("*.mc4")
  destinationDirectory.set(file("$buildDir/libs"))
  archiveBaseName.set(project.name)
  archiveClassifier.set("grammars")
  archiveVersion.set(project.version.toString())
}