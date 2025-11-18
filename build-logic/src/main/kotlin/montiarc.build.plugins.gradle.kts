/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.InjectVersionTask

plugins {
  id("java-gradle-plugin")
  id("montiarc.build.kotlin")
  id("montiarc.build.project-version")
  id("montiarc.build.repositories")
  id("montiarc.build.publish-base")
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/kotlin"))
    kotlin.srcDir(layout.buildDirectory.dir("montiarc-build/main/kotlin"))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.setSrcDirs(setOf("test/kotlin"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}

val injectVersion by tasks.registering(InjectVersionTask::class) {
  version.set("${project.version}")
  target.set(layout.buildDirectory.dir("montiarc-build/main/kotlin"))
  pkg.set("montiarc.gradle.${project.name.replace("-", ".")}")
}

tasks.compileKotlin { dependsOn(injectVersion) }
