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

  api("${libs.monticoreCD4Analysis}:${libs.monticoreVersion}")
  api("${libs.monticoreRuntime}:${libs.monticoreVersion}")

  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.javaSmt}:${libs.javaSmtVersion}")

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }
  testImplementation("${libs.fdLang}:${libs.monticoreVersion}")
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

