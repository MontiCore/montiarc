dependencyResolutionManagement {
  repositories {
    if (("true").equals(System.getProperty("useLocalRepo"))) {
      mavenLocal()
    }
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
    gradlePluginPortal()
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
    create("seLibs") {
      from("de.se_rwth.commons:se-commons-catalog:7.10.0-SNAPSHOT")
    }
  }
}
