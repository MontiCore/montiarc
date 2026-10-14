/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}

dependencies {
  api(project(":languages:montiarc"))

  implementation(libs.janino)
  implementation(libs.guava)
  implementation(seLibs.mc.fd)
  implementation(libs.z3)

  testImplementation(testFixtures(project(":languages:basis")))
}

tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "montiarc.arc2fd.Arc2FDTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("Arc2FD")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}
