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
include(":language")
include(":language:basis")
include(":language:automaton")
include(":language:comfy")
include(":language:compute")
include(":language:core")
include(":language:features")
include(":language:generics")
include(":language:modes")
include(":language:mode-transitions")
include(":language:montiarc")
include(":library:majava-rte")
include(":library:lejos-rte")
include(":library:simulator-rte")
include(":library:dynsim-rte")
include(":generator:cd2pojo")
include(":generator:ma2java")
include(":generator:ma2java-it")
include(":generator:ma2ktln")
include(":application:bumperbot")
include(":application:factory")
include(":application:factoryTask")
include(":application:lightControl")

