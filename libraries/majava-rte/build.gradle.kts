/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("maJava-rte")
  isZip64 = true
}
