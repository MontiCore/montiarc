/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VersionInjection.Companion.registerVersionInjection

plugins {
  id("montiarc.build.plugins")
}

group = "montiarc.tooling"

gradlePlugin {
  plugins {
    create("Cd2pojo") {
      id = "cd2pojo"
      implementationClass = "montiarc.tooling.cd2pojo.plugin.Cd2PojoPlugin"
    }
  }
}

val genDir4GeneratorVersionInjection = "${project.buildDir}/generatedKotlin"

sourceSets {
  main {
    java.srcDir(genDir4GeneratorVersionInjection)
  }
}

registerVersionInjection(
  taskName = "injectGeneratorVersion",
  genDir = genDir4GeneratorVersionInjection,
  packageName = "montiarc.tooling.cd2pojo.plugin",
  constantName = "GENERATOR_VERSION"
)

tasks.compileKotlin { dependsOn(tasks.getByName("injectGeneratorVersion")) }