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
    create("MontiArc") {
      id = "montiarc-jsim"
      implementationClass = "montiarc.gradle.ma2jsim.Ma2JavaPlugin"
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
  packageName = "montiarc.gradle.ma2jsim",
  constantName = "GENERATOR_VERSION"
)

tasks.compileKotlin { dependsOn(tasks.getByName("injectGeneratorVersion")) }
