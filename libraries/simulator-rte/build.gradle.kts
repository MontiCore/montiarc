/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

sourceSets.create("example") {
  java.srcDir("$projectDir/example/java")
  compileClasspath += sourceSets["main"].output
  runtimeClasspath += sourceSets["main"].output
}

dependencies {
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")

  testImplementation(sourceSets["example"].output)
}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("simulator-rte")
  isZip64 = true
}