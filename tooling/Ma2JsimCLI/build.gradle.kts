/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(project(":generators:ma2jsim"))
  implementation(project(":libraries:montiarc-base"))
}

sourceSets {
  main {
    resources {
      srcDir("${buildDir}/generated/resources")
    }
  }
}

tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "montiarc.Ma2JsimToolCLI"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("MontiArc")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

tasks.register<Copy>("copyMontiArcBaseResources") {
  from(rootProject.projectDir.absolutePath + "/libraries/montiarc-base/build/libs")
  include("*Symbols.jar")
  rename("montiarc-base-${version}-(.*)\\.jar", "montiarc-base-$1.zip")
  into("${buildDir}/generated/resources/montiarc")
}

tasks.register<Copy>("copySimulatorResources") {
  from(rootProject.projectDir.absolutePath + "/libraries/simulator-rte/build/libs")
  include("*Symbols.jar")
  rename("simulator-rte-${version}-(.*)\\.jar", "simulator-rte-$1.zip")
  into("${buildDir}/generated/resources/montiarc")
}

tasks.named("processResources") {
  dependsOn("copyMontiArcBaseResources", "copySimulatorResources")
}
