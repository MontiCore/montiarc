/* (c) https://github.com/MontiCore/monticore */

val se_commons_version: String by project
val junit_jupiter_version: String by project
val logback_version: String by project
val guava_version: String by project
val janino_version: String by project
val assertj_version: String by project

plugins {
  java
}

group = "montiarc.generators"

val hwcDir = "$projectDir/src/main/java"
val genDir = "$buildDir/generated-sources"

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"

sourceSets {
  main {
    java.srcDir("$genDir")
  }
}

configurations {
  create("generateCD")
  create("generateMA")
}

dependencies {
  val generateCD = configurations["generateCD"]
  val generateMA = configurations["generateMA"]

  // MontiCore dependencies
  implementation("de.se_rwth.commons:se-commons-logging:$se_commons_version")
  implementation("de.se_rwth.commons:se-commons-utilities:$se_commons_version")

  // Internal dependencies
  generateCD(project(":generators:cd2pojo"))
  generateMA(project(":generators:ma2java"))

  implementation(project(":language:montiarc"))
  implementation(project(":libraries:majava-rte"))

  // Guava
  implementation("com.google.guava:guava:$guava_version")

  // Janino
  implementation("org.codehaus.janino:janino:$janino_version")

  // Other dependencies
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")

  // AssertJ
  testImplementation("org.assertj:assertj-core:$assertj_version")

  // loggers for generators
  generateCD("ch.qos.logback:logback-core:$logback_version")
  generateCD("ch.qos.logback:logback-classic:$logback_version")
  generateMA("ch.qos.logback:logback-core:$logback_version")
  generateMA("ch.qos.logback:logback-classic:$logback_version")
}

tasks.test {
  useJUnitPlatform()
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath = configurations["generateCD"]
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/src/main/resources", genDir, hwcDir)
  outputs.dir(genDir)

  // Configuring logging during generation
  systemProperties["logback.configurationFile"] = generatorLogbackConfig
  systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
  systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-cd2pojo"

}


val genMaTask = tasks.register<JavaExec>("generateMontiArc") {
  classpath(configurations["generateMA"])
  mainClass.set("montiarc.generator.MontiArcTool")


  args("-mp", "$projectDir/src/main/resources")
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


genMaTask { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(genMaTask) }

genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
genMaTask { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }