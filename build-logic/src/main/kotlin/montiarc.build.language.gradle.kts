/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("monticore")
  id("montiarc.build.java-library")
}

tasks.register<de.monticore.MCTask>("grammar") {
  handcodedPath.add("$projectDir/main/java")
  modelPath.add(project(":language").projectDir.toString() + "/grammars")
  outputDir.set(file("$buildDir/sources/main/java/"))
}