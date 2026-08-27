/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

gradlePlugin {
  plugins {
    create("Class2MC") {
      id = "class2mc"
      implementationClass = "de.monticore.gradle.class2mc.Class2MCPlugin"
    }
  }
}

