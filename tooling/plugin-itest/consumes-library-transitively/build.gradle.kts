/* (c) https://github.com/MontiCore/monticore */
buildscript {
  dependencies {
    classpath("montiarc.tooling:plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc")
}

group = "montiarc.tooling.plugin-itest"

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  montiarc(project(":tooling:plugin-itest:consumes-library"))

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}