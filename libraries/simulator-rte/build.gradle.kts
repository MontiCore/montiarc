/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

val exampleSourceSet = sourceSets.create("example") {
  java.srcDir("$projectDir/example/java")
  compileClasspath += sourceSets.main.get().output
  runtimeClasspath += sourceSets.main.get().output
}

val exampleTestSourceSet = sourceSets.create("example-test") {
  java.srcDir("$projectDir/example-test/java")
  compileClasspath += sourceSets.main.get().output
  compileClasspath += exampleSourceSet.output
  runtimeClasspath += sourceSets.main.get().output
  runtimeClasspath += exampleSourceSet.output
}

val exampleTestImplementation: Configuration by configurations.getting {
  extendsFrom(configurations["exampleImplementation"])
  extendsFrom(configurations.testImplementation.get())
}

val exampleTestRuntimeOnly : Configuration by configurations.getting {
  extendsFrom(configurations["exampleRuntimeOnly"])
  extendsFrom(configurations.testRuntimeOnly.get())
}

dependencies {
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("simulator-rte")
  isZip64 = true
}

val exampleTestTask = task<Test>("exampleTest") {
  description = "Runs tests against the example."
  group = "verification"

  testClassesDirs = exampleTestSourceSet.output.classesDirs
  classpath = exampleTestSourceSet.runtimeClasspath
  shouldRunAfter("test")

  useJUnitPlatform()

  testLogging {
    events("passed")
  }
}

tasks.check { dependsOn(exampleTestTask) }
