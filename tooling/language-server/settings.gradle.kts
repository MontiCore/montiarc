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

rootProject.name = "language-server"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../../gradle/libs.versions.toml"))
    }
    create("seLibs") {
      from("de.se_rwth.commons:se-commons-catalog:7.10.0-SNAPSHOT")
    }
  }
}
