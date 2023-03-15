/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.integration-test")
  id("montiarc")
}

val cdModelDir = "$projectDir/main/resources"
val cdHWCDir = "$projectDir/main/java"
val cdGenJavaDir = "$buildDir/cd4a/main/java"
val cdGenSymDir = "$buildDir/cd4a/main/sym"

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
    java {
      srcDir(cdGenJavaDir)
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

val generateCD: Configuration = configurations.create("generateCD")

dependencies {
  generateCD(project(":generators:cd2pojo"))

  implementation(sourceSets["base"].output)
  implementation(project(":languages:montiarc"))
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
}

val compileCD4A = tasks.register<JavaExec>("compileCD4A") {
  classpath = generateCD
  mainClass.set("de.monticore.cd2pojo.CD2PojoTool")

  args("-i", cdModelDir)
  args("-c")
  args("-c2mc")
  args("-path", compileBaseJava.outputs.files.asPath)
  args("-o", cdGenJavaDir)
  args("-s", cdGenSymDir)
  args("-hwc", cdHWCDir)
  inputs.dir(cdModelDir)
  outputs.dirs(cdGenJavaDir, cdGenSymDir)
}

montiarc {
  internalMontiArcTesting.set(true)
}

val compileBaseJava: Task by tasks.getting

tasks.compileMontiarc {
  symbolImportDir.from(cdGenSymDir, compileBaseJava.property("destinationDirectory"))
  useClass2Mc.set(true)
}

compileBaseJava.dependsOn(tasks["compileBaseMontiarc"])
compileCD4A { dependsOn(compileBaseJava) }
tasks.compileMontiarc { dependsOn(compileCD4A) }
tasks.compileMontiarc { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }

compileCD4A { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
