/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("java-library")
  id("montiarc.build.publishing")
}

val se_commons_version: String by project
val mockito_version: String by project
val junit_jupiter_version: String by project

dependencies {
  api("de.se_rwth.commons:se-commons-logging:$se_commons_version")
  testImplementation("org.mockito:mockito-core:$mockito_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/java"))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.srcDirs(setOf("test/java"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}

tasks.getByName<Test>("test").useJUnitPlatform()