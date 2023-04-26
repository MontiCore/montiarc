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

includeBuild("../../build-logic")