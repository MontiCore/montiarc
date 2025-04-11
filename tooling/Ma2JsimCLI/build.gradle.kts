/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation(project(":generators:ma2jsim"))
  implementation(project(":libraries:montiarc-base"))
  implementation(project(":libraries:maunit"))
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
  val libraryName = "montiarc-base"
  dependsOn(rootProject.project(":libraries:${libraryName}").tasks.named("assemble"))
  from(rootProject.projectDir.absolutePath + "/libraries/${libraryName}/build/libs")
  include("*Symbols.jar")
  rename("${libraryName}-${version}-(.*)\\.jar", "${libraryName}-$1.zip")
  into("${buildDir}/generated/resources/montiarc")
}

tasks.register<Copy>("copySimulatorResources") {
  val libraryName = "simulator-rte"
  dependsOn(rootProject.project(":libraries:${libraryName}").tasks.named("assemble"))
  from(rootProject.projectDir.absolutePath + "/libraries/${libraryName}/build/libs")
  include("*Symbols.jar")
  rename("${libraryName}-${version}-(.*)\\.jar", "${libraryName}-$1.zip")
  into("${buildDir}/generated/resources/montiarc")
}

tasks.register<Copy>("copyMaUnitResources") {
  val libraryName = "maunit"
  dependsOn(rootProject.project(":libraries:${libraryName}").tasks.named("assemble"))
  from(rootProject.projectDir.absolutePath + "/libraries/${libraryName}/build/libs")
  include("*Symbols.jar")
  rename("${libraryName}-${version}-(.*)\\.jar", "${libraryName}-$1.zip")
  into("${buildDir}/generated/resources/montiarc")
}

tasks.named("processResources") {
  dependsOn("copyMontiArcBaseResources", "copySimulatorResources", "copyMaUnitResources")
}
