/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(libs.mc.grammar)
  grammar(libs.mc.sc)

  api(project(":languages:montiarc"))
  api(project(":languages:basis"))
  api(libs.mc.sc) {
    exclude("org.apache.groovy", "groovy")
  }

  implementation(libs.apache)
  implementation(libs.guava)
  implementation(libs.z3)
  implementation(libs.mc.cd4a)
  implementation(libs.mc.ocl.ocl2smt)
  implementation(variantOf(libs.mc.cd4a) { classifier("cd2smt") })
}

tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "montiarc.conformance.AutConformanceTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("MAConformance")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

tasks.register<JavaExec>("runDemo") {
  classpath = files(tasks.shadowJar.get().archiveFile)
  val modelDir = "test/resources/montiarc/conformance/demo/"
  args = listOf(
      "--reference", modelDir + "Reference.arc", modelDir + "Reference.cd",
      "--concrete", modelDir + "Concrete.arc", modelDir + "Concrete.cd",
      "--map", modelDir + "Mapping.map"
  )
}
