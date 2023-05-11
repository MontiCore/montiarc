/* (c) https://github.com/MontiCore/monticore */

plugins {
  java
  id("montiarc.build.repositories")
  id("montiarc.build.spotless")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
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
  useJUnitPlatform()
  systemProperty("buildDir", layout.buildDirectory.get().toString())
}

dependencies {
  testImplementation("org.assertj:assertj-core:3.23.1")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}