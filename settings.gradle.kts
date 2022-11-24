/* (c) https://github.com/MontiCore/monticore */

pluginManagement {
  repositories {
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
    gradlePluginPortal()
    maven {
      url = uri("build-logic/build/repo")
    }
  }
}

rootProject.name = "montiarc"

includeBuild("build-logic")

include(":base-platform")
include(":languages")
include(":languages:basis")
include(":languages:automaton")
include(":languages:comfy")
include(":languages:compute")
include(":languages:core")
include(":languages:features")
include(":languages:generics")
include(":languages:modes")
include(":languages:montiarc")
include(":languages:prepost")
include(":languages:syscl-basis")
include(":languages:syscl")
include(":libraries:majava-rte")
include(":libraries:lejos-rte")
include(":libraries:simulator-rte")
include(":libraries:dynsim-rte")
include(":libraries:dynsim-reaction-rte")
include(":generators:cd2pojo")
include(":generators:ma2java")
include(":generators:ma2java-it")
include(":applications:bumperbot")
include(":applications:elevator")
include(":tooling:language-server")
includeBuild("tooling/plugin")
