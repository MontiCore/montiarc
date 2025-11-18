/* (c) https://github.com/MontiCore/monticore */
import de.monticore.gradle.common.MCBuildInfoTask

plugins {
  id("montiarc.build.java-library")
}

val generateBuildInfo by tasks.registering(MCBuildInfoTask::class) {

  version.set(project.version.toString())

  buildInfoFile.set(project.layout.buildDirectory.file("montiarc-build/main/resources/buildInfo.properties"))
}

tasks.jar {
  // Make sure the file is generated before the jar task
  dependsOn(generateBuildInfo)

  // Add the generated file to the jar
  from(generateBuildInfo.map { it.buildInfoFile.get().asFile.parentFile }) {
    include("buildInfo.properties")
    into("")
  }
}
