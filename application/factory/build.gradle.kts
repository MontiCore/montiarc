/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

group = "montiarc.application"

val hwcDir = "$projectDir/main/java"
val genDir = "$buildDir/generated-sources"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets["main"].java {
  srcDir(genDir)
}

// Configurations
val generateCD = configurations.create("generateCD")
val generateMA = configurations.create("generateMA")

dependencies {
  generateCD(project(":generator:cd2pojo"))
  generateCD("${libs.logbackCore}:${libs.logbackVersion}")
  generateCD("${libs.logbackClassic}:${libs.logbackVersion}")
  generateMA(project(":generator:ma2java"))
  generateMA("${libs.logbackCore}:${libs.logbackVersion}")
  generateMA("${libs.logbackClassic}:${libs.logbackVersion}")

  api(project(":library:majava-rte"))
  api(project(":library:lejos-rte"))
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")

  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/main/resources", genDir, hwcDir)
  args("-c2mc")
  outputs.dir(genDir)

  // Configuring logging during generation
  systemProperties["logback.configurationFile"] = generatorLogbackConfig
  systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
  systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-cd2pojo"
}

val genMaTask = tasks.register<JavaExec>("generateMontiArc") {
  classpath(generateMA)
  mainClass.set("montiarc.generator.MontiArcTool")

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }

  args("-mp", "$projectDir/main/resources")
  args("-path", genDir)
  args("-o", genDir)
  args("-hwc", hwcDir)
  args("-c2mc")
  outputs.dir(genDir)

  // Configuring logging during generation
  systemProperties["logback.configurationFile"] = generatorLogbackConfig
  systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
  systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-ma2java"
}

// Setting up task dependencies
genMaTask { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(genMaTask) }

genCdTask { mustRunAfter(project(":generator:cd2pojo").tasks.withType(Test::class)) }
genMaTask { mustRunAfter(project(":generator:ma2java").tasks.withType(Test::class)) }
