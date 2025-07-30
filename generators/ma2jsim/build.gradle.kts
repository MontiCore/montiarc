/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjectionForUpToDateChecks

plugins {
  id("montiarc.build.java-library")
}

dependencies {
  api(project(":languages:montiarc"))
  implementation(project(":libraries:simulator-rte"))
  implementation(libs.freemarker)
  implementation(libs.format)
  implementation(libs.guava)
  implementation(libs.janino)
  implementation(libs.mc.ocl)

  testImplementation((project(":languages:basis"))) {
    capabilities {
      requireCapability("montiarc.languages:basis-tests")
    }
  }

  testImplementation(project(":generators:cd2pojo"))
  testImplementation(libs.mockito)

  // Provide API only for montiarc.generator.MA2JSimTest#testRun()
  testRuntimeOnly(libs.api.guardian)
}

sourceSets["main"].java {
  srcDir("${buildDir}/montiarc/main/java")
}

// Inject generator version information into java code for up to date checks
registerVersionInjectionForUpToDateChecks(
  taskName = "injectGeneratorVersion",
  genDir = "${project.buildDir}/generated-resources/main",
  subfolder = "montiarc/generator",
  fileName = "Ma2JsimToolVersion.txt",
)
