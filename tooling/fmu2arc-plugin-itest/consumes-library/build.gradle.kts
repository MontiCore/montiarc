plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("java")
  id("montiarc-jsim")
  id("fmu2arc")
}

group = "montiarc.tooling.fmu2arc-plugin-itest"

dependencies {
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
}

tasks.getByName<Test>("test") {
  this.enabled = false
}



