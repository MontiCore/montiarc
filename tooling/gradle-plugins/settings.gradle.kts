/* (c) https://github.com/MontiCore/monticore */

pluginManagement {
  repositories {
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
    gradlePluginPortal()
    maven {
      url = uri("../../build-logic/build/repo")
    }
  }
}

rootProject.name = "gradle-plugins"

include(":cd2pojo")
include(":cd4ma-dependencies")
include(":ma2java")
include(":montiarc-base")
include(":montiarc-dependencies")
include(":montiarc-sources")
