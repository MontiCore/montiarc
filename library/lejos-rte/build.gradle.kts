/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

group = "montiarc.library"

val hwcDir = "$projectDir/src/main/java"
val genDir = "$buildDir/generated-sources"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets {
  main {
    java.srcDirs(hwcDir, genDir)
  }
  create("models") {
    resources.srcDir("$projectDir/src/main/resources")
  }
}

java {
  registerFeature("models") {
    usingSourceSet(sourceSets["models"])
  }
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
  api("${libs.lejos}:${libs.lejosVersion}")
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/src/main/resources", genDir, hwcDir)
  outputs.dir(genDir)

  // Configuring logging during generation
  systemProperties["logback.configurationFile"] = generatorLogbackConfig
  systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
  systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-cd2pojo"
}

val genMaTask = tasks.register<JavaExec>("generateMontiArc") {
  classpath(generateMA)
  mainClass.set("montiarc.generator.MontiArcTool")


  args("-mp", "$projectDir/src/main/resources")
  args("-path", genDir)
  args("-o", genDir)
  args("-hwc", hwcDir)
  args("-c2mc")
  outputs.dir(genDir)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }

  // Configuring logging during generation
  systemProperties["logback.configurationFile"] = generatorLogbackConfig
  systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
  systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-ma2java"
}

genMaTask { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(genMaTask) }
tasks.named("modelsJar") { dependsOn(tasks.compileJava) }
