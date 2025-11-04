/* (c) https://github.com/MontiCore/monticore */
plugins {
  java
  application
  id("montiarc-jsim") version "7.8.1"
  id("cd2pojo") version "7.8.1"
}

application {
  if (hasProperty("mainClass")) {
    mainClass.set("${property("mainClass")}")
  }
}

repositories {
  mavenLocal()
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}

tasks.compileCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)
  //checkVariability.set(true)
}

tasks.compileTestCd2pojo {
  useClass2Mc.set(true)
}

tasks.compileTestMontiarc {
  useClass2Mc.set(true)
}
