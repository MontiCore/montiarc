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

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets["main"].java {
  srcDir(genDirMa)
  srcDir(genDirCd)
}

// Configurations
val generateCD = configurations.create("generateCD")

dependencies {
  generateCD(project(":generators:cd2pojo"))
  generateCD("${libs.logbackCore}:${libs.logbackVersion}")
  generateCD("${libs.logbackClassic}:${libs.logbackVersion}")

  api(project(":libraries:majava-rte"))
  api(project(":libraries:lejos-rte"))
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")

  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/main/resources", genDirCd, hwcDir)
  args("-c2mc")
  outputs.dir(genDirCd)

  // Configuring logging during generation
  systemProperties["logback.configurationFile"] = generatorLogbackConfig
  systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
  systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-cd2pojo"
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
