/* (c) https://github.com/MontiCore/monticore */

plugins {
  `java-library`
  id("montiarc.build.jacoco")
  id("montiarc.build.modules")
  id("montiarc.build.publish-java")
  id("montiarc.build.repositories")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

sourceSets {
  main {
    java {
      setSrcDirs(setOf(layout.projectDirectory.dir("main/java")))
    }
    resources {
      setSrcDirs(setOf(layout.projectDirectory.dir("main/resources")))
    }
  }
  test {
    java {
      setSrcDirs(setOf(layout.projectDirectory.dir("test/java")))
    }
    resources {
      setSrcDirs(setOf(layout.projectDirectory.dir("test/resources")))
    }
  }
}

tasks.test {
  systemProperty("buildDir", layout.buildDirectory.get().toString())
}