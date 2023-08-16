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
include(":libraries:majava-dse-rte")
include(":libraries:montiarc-base")
include(":libraries:simulator-rte")
include(":generators:arc2fd")
include(":generators:arc2fd-it")
include(":generators:cd2pojo")
include(":generators:cd2pojo-it")
include(":generators:ma2java")
include(":generators:ma2java-it")
include(":generators:ma2java-dse-it")
include(":generators:ma2javasim")
include(":generators:ma-java-it")
include(":applications:bumperbot")
include(":applications:elevator")
include(":tooling:language-server")
includeBuild("tooling/gradle-plugins")

// Integration test projects for the built gradle plugins
include("tooling:ma2java-plugin-itest:applies-default-values")
include("tooling:ma2java-plugin-itest:configuration-value-robustness")
include("tooling:ma2java-plugin-itest:produces-library")
include("tooling:ma2java-plugin-itest:consumes-library")
include("tooling:ma2java-plugin-itest:consumes-library-transitively")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:cd-a")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:cd-b")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:ma-only-c")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:ma-only-end-consumer")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:mixed-end-consumer")
include("tooling:ma2java-plugin-itest:cd4montiarc-dependencies:availability-in-tests")
include("tooling:ma2java-plugin-itest:just-applies-java-base-plugin")
include("tooling:cd2pojo-plugin-itest:produces-library")
include("tooling:cd2pojo-plugin-itest:consumes-library")
include("tooling:cd2pojo-plugin-itest:consumes-library-transitively")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      library("apache", "org.apache.commons:commons-lang3:3.12.0")
      library("format", "com.google.googlejavaformat:google-java-format:1.7")
      library("guava", "com.google.guava:guava:31.1-jre")
      library("janino", "org.codehaus.janino:janino:3.1.9")
      library("smt", "org.sosy-lab:java-smt:3.12.0")
      library("mockito", "org.mockito:mockito-core:5.3.1")
      library("z3", "tools.aqua:z3-turnkey:4.11.2")

      library("mc-lsp", "de.monticore.language-server:monticore-language-server-runtime:7.6.0-SNAPSHOT")
      library("mc-grammar", "de.monticore:monticore-grammar:7.6.0-SNAPSHOT")
      library("mc-runtime", "de.monticore:monticore-runtime:7.6.0-SNAPSHOT")
      library("mc-c2mc", "de.monticore:class2mc:7.6.0-SNAPSHOT")
      library("se-groovy", "de.se_rwth.commons:se-commons-groovy:7.6.0-SNAPSHOT")
      library("se-logging", "de.se_rwth.commons:se-commons-logging:7.6.0-SNAPSHOT")
      library("se-utilities", "de.se_rwth.commons:se-commons-utilities:7.6.0-SNAPSHOT")
      library("mc-cd4a", "de.monticore.lang:cd4analysis:7.6.0-SNAPSHOT")
      library("mc-ocl", "de.monticore.lang:ocl:7.6.0-SNAPSHOT")
      library("mc-sc", "de.monticore.lang:statecharts:7.6.0-SNAPSHOT")
      library("mc-fd", "de.monticore.lang:fd-lang:7.6.0-SNAPSHOT")
    }
  }
}
