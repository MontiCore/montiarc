/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjectionForPlugins

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

dependencies {
  implementation(project(":cd2pojo"))
  implementation(project(":montiarc-base"))
}

gradlePlugin {
  plugins {
    create("Montiarc") {
      id = "montiarc"
      implementationClass = "montiarc.gradle.ma2java.Ma2JavaPlugin"
    }
  }
}

val genDir4GeneratorVersionInjection = "${project.buildDir}/generatedKotlin"

sourceSets {
  main {
    kotlin.srcDir(genDir4GeneratorVersionInjection)
  }
}

registerVersionInjectionForPlugins(
  taskName = "injectGeneratorVersion",
  genDir = genDir4GeneratorVersionInjection,
  packageName = "montiarc.gradle.ma2java",
  constantName = "GENERATOR_VERSION"
)

tasks.compileKotlin { dependsOn(tasks.getByName("injectGeneratorVersion")) }
