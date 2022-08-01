/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("monticore")
  id("montiarc.build.java-library")
}

val grammarTask = tasks.register<de.monticore.MCTask>("grammar") {
  handcodedPath.add("$projectDir/main/java")
  modelPath.add(project(":languages").projectDir.toString() + "/grammars")
  outputDir.set(layout.buildDirectory.dir("sources/main/java/"))
}

project.the<SourceSetContainer>()["main"].java {
  srcDirs(grammarTask.get().getOutputDir())
}
