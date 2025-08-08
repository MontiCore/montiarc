/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
  id("montiarc.build.java-library")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

dependencies {
  grammar(seLibs.mc.grammar)
  grammar(seLibs.mc.statecharts)

  api(project(":languages:montiarc"))
  api(project(":languages:basis"))
  api(seLibs.mc.statecharts) {
    exclude("org.apache.groovy", "groovy")
  }

  implementation(libs.apache.commons)
  implementation(libs.guava)
  implementation(libs.z3)
  implementation(seLibs.mc.cd4a)
  implementation(seLibs.mc.ocl.ocl2smt)
  implementation(variantOf(seLibs.mc.cd4a) { classifier("cd2smt") })
}
