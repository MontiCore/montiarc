/* (c) https://github.com/MontiCore/monticore */

pluginManagement {
  includeBuild("./build-logic")

  repositories {
    if (("true").equals(System.getProperty("useLocalRepo"))) {
      mavenLocal()
    }
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    if (("true").equals(System.getProperty("useLocalRepo"))) {
      mavenLocal()
    }
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
  }
  versionCatalogs {
    create("seLibs") {
      from("de.se_rwth.commons:se-commons-catalog:7.10.0-SNAPSHOT")
    }
  }
}

gradle.beforeProject {
  configurations.all {
    resolutionStrategy.dependencySubstitution {
      // The montiarc gradle-plugins declare external dependencies.
      // We substitute these with local project dependencies here
      // so that tests and applications run on the latest local changes.
      substitute(module("montiarc.generators:cd2pojo"))
        .using(project(":generators:cd2pojo"))
      substitute(module("montiarc.generators:ma2java"))
        .using(project(":generators:ma2java"))
      substitute(module("montiarc.generators:ma2jsim"))
        .using(project(":generators:ma2jsim"))
      substitute(module("montiarc.generators:sd2arc"))
        .using(project(":generators:sd2arc"))
      substitute(module("montiarc.generators:fmu2arc"))
        .using(project(":generators:fmu2arc"))
      substitute(module("montiarc.libraries:majava-rte"))
        .using(project(":libraries:majava-rte"))
      substitute(module("montiarc.libraries:simulator-rte"))
        .using(project(":libraries:simulator-rte"))
      substitute(module("montiarc.libraries:simulator-test-rte"))
        .using(project(":libraries:simulator-test-rte"))
      substitute(module("montiarc.libraries:montiarc-base"))
        .using(project(":libraries:montiarc-base"))
      substitute(module("montiarc.libraries:maunit"))
        .using(project(":libraries:maunit"))
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
include(":languages:features")
include(":languages:modes")
include(":languages:montiarc")
include(":languages:prepost")
include(":languages:syscl-basis")
include(":languages:syscl")
include(":languages:ucd")
include(":libraries:majava-rte")
include(":libraries:majava-dse-rte")
include(":libraries:montiarc-base")
include(":libraries:maunit")
include(":libraries:simulator-rte")
include(":libraries:simulator-test-rte")
include(":generators:arc2fd")
include(":generators:arc2fd-it")
include(":generators:cd2pojo")
include(":generators:cd2pojo-it")
include(":generators:sd2arc")
include(":generators:sd2arc-it")
include(":generators:fmu2arc")
include(":generators:fmu2arc-it")
include(":generators:ma2java")
include(":generators:ma2java-it")
include(":generators:ma2java-dse-it")
include(":generators:ma2jsim")
include(":generators:ma2jsim-it")
include(":generators:ma-java-it")
include(":applications:avionics")
include(":applications:bumperbot")
include(":applications:elevator")
include(":applications:factory")
include(":applications:tutorial")
include(":languages:conformance")
include(":languages:effect")
include(":tooling:language-server")
include(":tooling:montiarc-intellij-plugin")
include(":tooling:ma2jsim-cli")
includeBuild("tooling/gradle-plugins")

// Integration test projects for the built gradle plugins
include("tooling:ma2java-plugin-itest:main-cds-are-available-to-test-arcs")
include("tooling:ma2jsim-plugin-itest:applies-default-values")
include("tooling:ma2jsim-plugin-itest:configuration-value-robustness")
include("tooling:ma2jsim-plugin-itest:produces-library")
include("tooling:ma2jsim-plugin-itest:consumes-library")
include("tooling:ma2jsim-plugin-itest:consumes-library-transitively")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:cd-a")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:cd-b")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:ma-only-c")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:ma-only-end-consumer")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:mixed-end-consumer")
include("tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:availability-in-tests")
include("tooling:ma2jsim-plugin-itest:just-applies-java-base-plugin")
include("tooling:cd2pojo-plugin-itest:produces-library")
include("tooling:cd2pojo-plugin-itest:consumes-library")
include("tooling:cd2pojo-plugin-itest:consumes-library-transitively")
include("tooling:fmu2arc-plugin-itest:consumes-library")
include("tooling:fmu2arc-plugin-itest:consumes-library-transitively")
