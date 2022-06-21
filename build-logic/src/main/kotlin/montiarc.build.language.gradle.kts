import gradle.kotlin.dsl.accessors._e115dd7072ba4d98136045faa736bf3c.reporting

/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("monticore")
  id("montiarc.build.java-library")
}

tasks.register<de.monticore.MCTask>("grammar") {
  handcodedPath.add("$projectDir/main/java")
  modelPath.add(project(":languages").projectDir.toString() + "/grammars")
  outputDir.set(layout.buildDirectory.dir("sources/main/java/"))
}