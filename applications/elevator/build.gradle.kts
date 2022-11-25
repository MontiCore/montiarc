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
val genDirMa = "$genDir/montiarc"

sourceSets["main"].java {
  srcDir(genDirMa)
  srcDir(genDirCd)
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
  modelPath.from("$projectDir/main/resources")
  symbolImportDir.from(genDirCd)
  useClass2Mc.set(true)
  hwcPath.from(hwcDir)
  outputDir.set(genDirMa)

  internalMontiArcTesting.set(true)
}

// Setting up task dependencies
tasks.compileMontiarc { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(tasks.compileMontiarc) }

genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
