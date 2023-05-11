/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

tasks.test {
  useJUnitPlatform()
}

dependencies {
  api(project(":languages:montiarc"))

  api(libs.mc.cd4a)
  api(libs.mc.runtime)

  implementation(libs.janino)
  implementation(libs.guava)
  implementation(libs.smt)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }
  testImplementation(libs.mc.fd)
}

java {
  withSourcesJar()
}

tasks.shadowJar {
  minimize()
  manifest {
    attributes["Main-Class"] = "montiarc.arc2fd.FDGenerator"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("Arc2FD")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

