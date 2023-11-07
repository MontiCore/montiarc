/* (c) https://github.com/MontiCore/monticore */

plugins {
  java
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.spotless")
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/java"))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.setSrcDirs(setOf("test/java"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}

tasks.test {
  useJUnitPlatform()
  systemProperty("buildDir", layout.buildDirectory.get().toString())
}

dependencies {
  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}