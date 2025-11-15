/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjectionForPlugins

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

dependencies {
  implementation(project(":cd2pojo"))
  implementation(project(":ma2jsim"))
  implementation(project(":ma2java"))
  implementation(project(":montiarc-sources"))
}

gradlePlugin {
  plugins {
    create("Sd2arc") {
      id = "sd2arc"
      implementationClass = "montiarc.gradle.sd2arc.Sd2ArcPlugin"
    }
  }
}

val genDir4GeneratorVersionInjection = layout.buildDirectory.dir("generatedKotlin")

sourceSets {
  main {
    kotlin.srcDir(genDir4GeneratorVersionInjection)
  }
}

registerVersionInjectionForPlugins(
  taskName = "injectGeneratorVersion",
  genDir = genDir4GeneratorVersionInjection.get().asFile.absolutePath,
  packageName = "montiarc.gradle.sd2arc",
  constantName = "GENERATOR_VERSION"
)

tasks.compileKotlin { dependsOn(tasks.getByName("injectGeneratorVersion")) }
