/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjection
import java.nio.file.Files

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling"
dependencies {
  implementation("montiarc.tooling:cd2pojo-plugin:${project.version}")
}

gradlePlugin {
  plugins {
    create("Montiarc") {
      id = "montiarc"
      implementationClass = "montiarc.tooling.plugin.MontiarcPlugin"
    }
  }
}

val genDir4GeneratorVersionInjection = "${project.buildDir}/generatedKotlin"

sourceSets {
  main {
    kotlin.srcDir(genDir4GeneratorVersionInjection)
  }
}

registerVersionInjection(
  taskName = "injectGeneratorVersion",
  genDir = genDir4GeneratorVersionInjection,
  packageName = "montiarc.tooling.plugin",
  constantName = "GENERATOR_VERSION"
)

tasks.compileKotlin { dependsOn(tasks.getByName("injectGeneratorVersion")) }