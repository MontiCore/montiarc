/* (c) https://github.com/MontiCore/monticore */

buildscript {
  dependencies {
    classpath("montiarc.tooling:plugin")
  }
}

plugins {
  id("montiarc.build.java-library")
  id("montiarc")
}

val hwcDir = "$projectDir/main/java"
val genDir = "$buildDir/generated-sources"
val genDirCd = "$genDir/cd"

sourceSets {
  main {
    java.srcDir(genDirCd)
    montiarc.srcDir("$projectDir/main/resources")
  }
}

// Configurations
val generateCD = configurations.create("generateCD")

dependencies {
  generateCD(project(":generators:cd2pojo"))

  api(project(":libraries:majava-rte"))
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/main/resources", genDirCd, hwcDir)
  args("-c2mc")
  outputs.dir(genDirCd)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileMontiarc {
  symbolImportDir.from(genDirCd)
  useClass2Mc.set(true)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

// Setting up task dependencies
tasks.compileMontiarc { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(tasks.compileMontiarc) }

genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
