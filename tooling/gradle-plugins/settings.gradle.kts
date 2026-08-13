/* (c) https://github.com/MontiCore/monticore */

pluginManagement {
  includeBuild("../../build-logic")

  repositories {
    if ("true" == System.getProperty("useLocalRepo")) {
      mavenLocal()
    }
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
    gradlePluginPortal()
  }
}

rootProject.name = "gradle-plugins"

include(":cd2pojo")
include(":cd4ma-dependencies")
include(":fmu2arc")
include(":fmu4ma-dependencies")
include(":class2mc")
include(":sd2arc")
include(":ma2java")
include(":ma2jsim")
include(":montiarc-base")
include(":montiarc-dependencies")
include(":montiarc-sources")
