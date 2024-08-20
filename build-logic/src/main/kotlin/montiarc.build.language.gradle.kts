/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("de.monticore.generator")
  id("montiarc.build.java-library")
}

sourceSets {
  main {
    grammars.setSrcDirs(setOf("main/grammars"))
  }
  test {
    grammars.setSrcDirs(setOf("test/grammars"))
  }
}
