/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java")
  id("java-test-fixtures")
}

sourceSets {
  testFixtures {
    java.setSrcDirs(setOf("testFixtures/java"))
    java.exclude("*.mc4")
    resources.setSrcDirs(setOf("test-fixtures/resources"))
  }
}
