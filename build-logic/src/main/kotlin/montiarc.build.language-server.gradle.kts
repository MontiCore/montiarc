/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
  id("com.github.node-gradle.node")
  id("de.monticore.language-server")
}

node {
  download.set(true)
  version.set("20.19.4")
}
