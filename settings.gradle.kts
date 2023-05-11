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
include(":generators:ma-java-it")
include(":applications:bumperbot")
include(":applications:elevator")
include(":tooling:language-server")
includeBuild("tooling/plugin")
includeBuild("tooling/cd2pojo-plugin")
include("tooling:plugin-itest:applies-default-values")
include("tooling:plugin-itest:configuration-value-robustness")
include("tooling:plugin-itest:produces-library")
include("tooling:plugin-itest:consumes-library")
include("tooling:plugin-itest:consumes-library-transitively")
include("tooling:plugin-itest:just-applies-java-base-plugin")
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
      library("mockito", "org.mockito:mockito-core:4.9.0")
      library("z3", "tools.aqua:z3-turnkey:4.11.2")

      library("mc-lsp", "de.monticore.language-server:monticore-language-server-runtime:7.5.0-SNAPSHOT")
      library("mc-grammar", "de.monticore:monticore-grammar:7.5.0-SNAPSHOT")
      library("mc-runtime", "de.monticore:monticore-runtime:7.5.0-SNAPSHOT")
      library("mc-c2mc", "de.monticore:class2mc:7.5.0-SNAPSHOT")
      library("se-groovy", "de.se_rwth.commons:se-commons-groovy:7.5.0-SNAPSHOT")
      library("se-logging", "de.se_rwth.commons:se-commons-logging:7.5.0-SNAPSHOT")
      library("se-utilities", "de.se_rwth.commons:se-commons-utilities:7.5.0-SNAPSHOT")
      library("mc-cd4a", "de.monticore.lang:cd4analysis:7.5.0-SNAPSHOT")
      library("mc-ocl", "de.monticore.lang:ocl:7.5.0-SNAPSHOT")
      library("mc-sc", "de.monticore.lang:statecharts:7.5.0-SNAPSHOT")
      library("mc-fd", "de.monticore.lang:fd-lang:7.5.0-SNAPSHOT")
    }
  }
}
