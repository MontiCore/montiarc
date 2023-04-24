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
include(":languages:ag")
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
include(":libraries:montiarc-base")
include(":libraries:simulator-rte")
include(":generators:arc2fd")
include(":generators:arc2fd-it")
include(":generators:cd2pojo")
include(":generators:cd2pojo-it")
include(":generators:ma2java")
include(":generators:ma2java-it")
include(":generators:ma2java-sym-it")
include(":generators:ma-java-it")
include(":applications:bumperbot")
include(":applications:elevator")
include(":tooling:language-server")
includeBuild("tooling/plugin")
include("tooling:plugin-itest:applies-default-values")
include("tooling:plugin-itest:configuration-value-robustness")
include("tooling:plugin-itest:produces-library")
include("tooling:plugin-itest:consumes-library")
include("tooling:plugin-itest:consumes-library-transitively")
include("tooling:plugin-itest:just-applies-java-base-plugin")
