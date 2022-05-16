/* (c) https://github.com/MontiCore/monticore */

val se_commons_version: String by project
val lejos_version: String by project
val junit_jupiter_version: String by project
val logback_version: String by project
val librarymodels_classifier: String by project

plugins {
  java
  id("montiarc.build.publishing")
}

group = "montiarc.applications"

val hwcDir = "$projectDir/src/main/java"
val genDir = "$buildDir/generated-sources"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets["main"].java {
  srcDir(genDir)
}

// Configurations
val generateCD = configurations.create("generateCD")
val generateMA = configurations.create("generateMA")
val librarymodels = configurations.create("librarymodels")

dependencies {
  // MontiCore dependencies
  implementation("de.se_rwth.commons:se-commons-logging:$se_commons_version")
  implementation("de.se_rwth.commons:se-commons-utilities:$se_commons_version")

  // Internal dependencies
  generateCD(project(":generators:cd2pojo"))
  generateMA(project(":generators:ma2java"))
  librarymodels(project(":libraries:lejos-rte")) {
    capabilities { requireCapability("montiarc.libraries:lejos-rte-$librarymodels_classifier") }
  }
  implementation(project(":language:montiarc"))
  implementation(project(":libraries:majava-rte"))
  implementation(project(":libraries:lejos-rte"))

  // Other dependencies
  implementation("lejos.nxt:classes:$lejos_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")

  // Loggers for generators
  generateCD("ch.qos.logback:logback-core:$logback_version")
  generateCD("ch.qos.logback:logback-classic:$logback_version")
  generateMA("ch.qos.logback:logback-core:$logback_version")
  generateMA("ch.qos.logback:logback-classic:$logback_version")
}

val unpackLibModelsTask = tasks.register<Sync>("unpackLibrarymodels") {
  dependsOn(librarymodels)

  from( librarymodels.map { zipTree(it) } )
  into("$buildDir/$librarymodels_classifier")
  exclude("META-INF/")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/src/main/resources, $buildDir/$librarymodels_classifier", genDir, hwcDir)
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

  args("-mp", "$projectDir/src/main/resources", "$buildDir/$librarymodels_classifier")
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
genCdTask { dependsOn(unpackLibModelsTask) }
genMaTask { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(genMaTask) }

genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
genMaTask { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }