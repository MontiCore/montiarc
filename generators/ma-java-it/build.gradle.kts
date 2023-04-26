/* (c) https://github.com/MontiCore/monticore */
import montiarc.tooling.cd2pojo.plugin.Cd2PojoCompile

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
      setSrcDirs(setOf("$projectDir/main/resources"))
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
  implementation(sourceSets["base"].output)
  implementation(project(":languages:montiarc"))
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
}

cd2pojo {
  internalMontiArcTesting.set(true)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileCd2pojo {
  symbolImportDir.setFrom(compileBaseJava.outputs.files)
  useClass2Mc.set(true)
}

val compileBaseJava: Task by tasks.getting

tasks.compileMontiarc {
  symbolImportDir.from(compileBaseJava.property("destinationDirectory"))
  useClass2Mc.set(true)
}

compileBaseJava.dependsOn(tasks["compileBaseMontiarc"])
tasks.compileCd2pojo { dependsOn(compileBaseJava) }
tasks.compileMontiarc { dependsOn(tasks.compileCd2pojo) }
