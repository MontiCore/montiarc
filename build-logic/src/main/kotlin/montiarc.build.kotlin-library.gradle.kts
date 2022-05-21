/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("montiarc.build.java-library")
}

kotlin.sourceSets["main"].kotlin.setSrcDirs(setOf("main/kotlin"))
kotlin.sourceSets["main"].resources.setSrcDirs(setOf("main/resources"))
kotlin.sourceSets["test"].kotlin.setSrcDirs(setOf("test/kotlin"))
kotlin.sourceSets["test"].resources.setSrcDirs(setOf("test/resources"))
kotlin.sourceSets["main"].kotlin.srcDir(java.sourceSets["main"].java.srcDirs)