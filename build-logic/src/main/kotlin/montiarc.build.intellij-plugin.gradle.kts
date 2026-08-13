/* (c) https://github.com/MontiCore/monticore */
import org.gradle.accessors.dm.LibrariesForSeLibs

plugins {
  id("montiarc.build.java")
  id("montiarc.build.nodejs")

  id("de.monticore.language-server")
  id("org.jetbrains.intellij.platform")
}

repositories {
  intellijPlatform {
    defaultRepositories()
  }
}

//https://github.com/gradle/gradle/issues/15383
val seLibs = the<LibrariesForSeLibs>()

dependencies {
  implementation(seLibs.mc.lsp.intellij.get())
  intellijPlatform {
    intellijIdeaCommunity("2025.2")

    bundledPlugin("com.intellij.java")
    plugin("com.redhat.devtools.lsp4ij", "0.7.0")
  }
}

tasks.register("initializeIntelliJPlugin") {
  // ignore, workaround for new intellij plugin
}
