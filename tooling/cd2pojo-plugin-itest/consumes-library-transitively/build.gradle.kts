/* (c) https://github.com/MontiCore/monticore */
buildscript {
  dependencies {
    classpath("montiarc.tooling:cd2pojo-plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.modules")
  id("montiarc.build.project-version")

  id("java-library")
  id("cd2pojo")
}

group = "montiarc.tooling.cd2pojo-plugin-itest"

cd2pojo {
  internalMontiArcTesting.set(true)
}

dependencies {
  cd2pojo(project(":tooling:cd2pojo-plugin-itest:consumes-library"))

  implementation("${libs.seCommonsLogging}:${project.version}")
  implementation("${libs.seCommonsUtils}:${project.version}")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}