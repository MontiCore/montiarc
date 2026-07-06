

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java-library")
  id("fmu2arc")
  id("montiarc-jsim")
}

group = "montiarc.tooling.fmu2arc-plugin-itest"

dependencies {
  fmu2arc(project(":tooling:fmu2arc-plugin-itest:consumes-library"))

  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.junit.platform.launcher)

}

tasks.compileMontiarc {
  useClass2Mc.set(true)
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
