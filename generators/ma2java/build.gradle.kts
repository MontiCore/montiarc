/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjectionForUpToDateChecks

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api(project(":languages:montiarc"))
  implementation(project(":libraries:majava-rte"))
  implementation(libs.format)
  implementation(libs.guava)
  implementation(libs.janino)

  testImplementation(project(":generators:cd2pojo"))
  testImplementation(libs.mc.c2mc)
  testImplementation(libs.mockito)
}

// All in one tool-jar
tasks.shadowJar {
  //minimize()
  manifest {
    attributes["Main-Class"] = "montiarc.generator.MA2JavaTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("MontiArc2Java")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

// Inject generator version information into java code for up to date checks
registerVersionInjectionForUpToDateChecks(
  taskName = "injectGeneratorVersion",
  genDir = "${project.buildDir}/generated-resources/main",
  subfolder = "montiarc/generator",
  fileName = "MA2JavaToolVersion.txt",
)
