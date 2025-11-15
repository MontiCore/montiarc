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
      srcDir(layout.buildDirectory.dir("generated/resources"))
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

fun registerSymbolCopyTask(taskName: String, libraryName: String) {
  tasks.register<Copy>(taskName) {
    val libProject = rootProject.project(":libraries:${libraryName}")
    dependsOn(libProject.tasks.named("assemble"))
    from(rootProject.projectDir.absolutePath + "/libraries/${libraryName}/build/libs")
    include("*Symbols.jar")
    rename("${libraryName}-${version}-(.*)\\.jar", "${libraryName}-$1.zip")
    into(layout.buildDirectory.dir("generated/resources/montiarc"))
  }
}

registerSymbolCopyTask("copyMontiArcBaseResources", "montiarc-base")
registerSymbolCopyTask("copySimulatorResources", "simulator-rte")
registerSymbolCopyTask("copyMaUnitResources", "maunit")

tasks.named("processResources") {
  dependsOn("copyMontiArcBaseResources", "copySimulatorResources", "copyMaUnitResources")
}

tasks.named("sourcesJar") {
  dependsOn("processResources")
}
