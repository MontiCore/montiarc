/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjection

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling.gradle-plugins"

gradlePlugin {
  plugins {
    create("Cd2pojo") {
      id = "cd2pojo"
      implementationClass = "montiarc.gradle.cd2pojo.Cd2PojoPlugin"
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
  packageName = "montiarc.gradle.cd2pojo",
  constantName = "GENERATOR_VERSION"
)

tasks.compileKotlin { dependsOn(tasks.getByName("injectGeneratorVersion")) }
