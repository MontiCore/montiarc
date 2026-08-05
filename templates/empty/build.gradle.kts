/* (c) https://github.com/MontiCore/monticore */
plugins {
  java
  application
  id("montiarc-jsim") version "7.10.0-SNAPSHOT"
  id("cd2pojo") version "7.10.0-SNAPSHOT"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

application {
  if (hasProperty("mainClass")) {
    mainClass.set("${property("mainClass")}")
  }
}

repositories {
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}

tasks.compileCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)
}

tasks.compileTestCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)
}
