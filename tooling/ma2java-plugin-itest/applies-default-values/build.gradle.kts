/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc")
}

group = "montiarc.tooling.ma2java-plugin-itest"

montiarc {
  internalMontiArcTesting.set(true)
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
