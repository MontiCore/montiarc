/* (c) https://github.com/MontiCore/monticore */
import montiarc.gradle.ma2java.MontiArcCompile

plugins {
  id("montiarc.build.integration-test")
  id("montiarc")
  id("cd2pojo")
}

sourceSets {
  create("base") {
    java {
      setSrcDirs(setOf("$projectDir/base/java"))
    }
    resources {
      setSrcDirs(setOf("$projectDir/base/resources"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/base/montiarc"))
    }
  }
  main {
    cd2pojo {
      setSrcDirs(setOf("$projectDir/main/cd2pojo"))
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
    }
  }
}

val baseImplementation: Configuration by configurations.getting

configurations {
  implementation.configure {
    extendsFrom(baseImplementation)
  }
  runtimeOnly.configure {
    extendsFrom(configurations.named("baseRuntimeOnly").get())
  }
}

dependencies {
  cd2pojoSymbolpath(sourceSets["base"].output)
  montiarcSymbolpath(sourceSets["base"].output)
  implementation(sourceSets["base"].output)
  implementation(seLibs.se.commons.logging)
  implementation(seLibs.se.commons.utilities)
  implementation(libs.guava)
  implementation(libs.janino)
}

val compileBaseMontiArc : TaskProvider<MontiArcCompile> = tasks.named<MontiArcCompile>("compileBaseMontiarc")

val compileBaseJava: TaskProvider<JavaCompile> = tasks.named<JavaCompile>("compileBaseJava") {
  dependsOn(compileBaseMontiArc)
}

tasks.compileCd2pojo {
  dependsOn(compileBaseJava)
  useClass2Mc.set(true)
}

tasks.compileMontiarc {
  useClass2Mc.set(true)
}
