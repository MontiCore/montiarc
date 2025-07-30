/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
  id("montiarc.build.java-library")
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

  implementation(libs.apache.commons)
  implementation(libs.guava)
  implementation(libs.z3)
  implementation(libs.mc.cd4a)
  implementation(libs.mc.ocl.ocl2smt)
  implementation(variantOf(libs.mc.cd4a) { classifier("cd2smt") })
}
