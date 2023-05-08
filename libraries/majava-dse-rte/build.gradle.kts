/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc.build.shadow")
}

dependencies {
  api("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  api("tools.aqua:z3-turnkey:4.8.17")
  api(project(":libraries:majava-rte"))
  api("${libs.seCommonsUtils}:${libs.monticoreVersion}")
  api("${libs.guava}:${libs.guavaVersion}")
  api("${libs.codehausJanino}:${libs.codehausVersion}")

  implementation(group= "org.apache.commons", name= "commons-lang3", version= "3.12.0")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")

}

tasks.shadowJar {
  minimize()
  archiveBaseName.set("ma2Java-symbolic-rte")
  isZip64 = true
}
