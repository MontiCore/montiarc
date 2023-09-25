/* (c) https://github.com/MontiCore/monticore */

pluginManagement {
  includeBuild("../../build-logic")

  repositories {
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
    gradlePluginPortal()
  }
}

rootProject.name = "gradle-plugins"

include(":cd2pojo")
include(":cd4ma-dependencies")
include(":ma2java")
include(":ma2jsim")
include(":montiarc-base")
include(":montiarc-dependencies")
include(":montiarc-sources")
