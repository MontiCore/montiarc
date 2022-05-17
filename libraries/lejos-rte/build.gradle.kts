/* (c) https://github.com/MontiCore/monticore */

val se_commons_version: String by project
val lejos_version: String by project
val junit_jupiter_version: String by project
val logback_version: String by project
val librarymodels_classifier: String by project
val mockito_version: String by project

plugins {
  java
  id("montiarc.build.java-library")
}

group = "montiarc.libraries"

val hwcDir = "$projectDir/src/main/java"
val genDir = "$buildDir/generated-sources"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets {
  main {
    java.srcDirs(hwcDir, genDir)
  }
  create("librarymodels") {
    resources.srcDir("$projectDir/src/main/resources")
  }
}

java {
  registerFeature(librarymodels_classifier) {
    usingSourceSet(sourceSets["librarymodels"])
  }
}

// Configurations
val generateCD = configurations.create("generateCD")
val generateMA = configurations.create("generateMA")

dependencies {
  // MontiCore dependencies
  implementation("de.se_rwth.commons:se-commons-logging:$se_commons_version")

  // Internal dependencies
  generateCD(project(":generators:cd2pojo"))
  generateMA(project(":generators:ma2java"))
  implementation(project(":libraries:majava-rte"))

  // Other dependencies
  implementation("lejos.nxt:classes:$lejos_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")
  testImplementation("org.mockito:mockito-core:$mockito_version")

  // Loggers for generators
  generateCD("ch.qos.logback:logback-core:$logback_version")
  generateCD("ch.qos.logback:logback-classic:$logback_version")
  generateMA("ch.qos.logback:logback-core:$logback_version")
  generateMA("ch.qos.logback:logback-classic:$logback_version")
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
tasks.named("librarymodelsJar") { dependsOn(tasks.compileJava) }
