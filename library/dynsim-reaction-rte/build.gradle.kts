/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

val hwcDir = "$projectDir/main/java"
val genDir = "$buildDir/generated-sources"
val modDir = "$buildDir/models/"

sourceSets {
  main {
    java.srcDirs(genDir)
  }
  create("model") {
    resources.srcDir(modDir)
  }
}

java {
  registerFeature("model") {
    usingSourceSet(sourceSets["model"])
  }
}

val generatorLogbackConfig = "$projectDir/logback.xml"
val generatorLogbackOutDir = "$buildDir/logs"
val generateSymbolTable = configurations.create("generateSymbolTable")

dependencies {
  api(project(":language:mode-transitions"))
  api(project(":language:montiarc"))
  api(project(":generator:cd2pojo"))

  generateSymbolTable(project(":language:montiarc"))

  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("${libs.codehausGroovy}:${libs.codehausVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.javaParser}:${libs.javaParserVersion}")
  implementation("${libs.monticoreClass2MC}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsGroovy}:${libs.monticoreVersion}")
  implementation("${libs.monticoreClass2MC}:${libs.monticoreVersion}")
  implementation("${libs.apacheBcel}:${libs.apacheBcelVersion}")

  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
}

val genSymbolTable = tasks.register<JavaExec>("generateSymbolTable") {
  classpath(tasks.compileJava, generateSymbolTable)
  mainClass.set("openmodeautomata.addition.AdderTool")

  args("-s", modDir)
  args("-f", "ReactionRuntimeSymbols.sym")
  args("-c2mc")
  args("-full")
  args("-p")
  outputs.dir(genDir)

  // Configuring logging during generation
  systemProperties["logback.configurationFile"] = generatorLogbackConfig
  systemProperties["LOGBACK_TARGET_DIR"] = generatorLogbackOutDir
  systemProperties["LOGBACK_TARGET_FILE_NAME"] = "logback-cd2pojo"
}

java {
  //withJavadocJar()
  withSourcesJar()
}

genSymbolTable { dependsOn(tasks.compileJava) }

genSymbolTable { mustRunAfter(project(":generator:cd2pojo").tasks.withType(Test::class)) }

tasks.named("modelJar") { dependsOn(genSymbolTable) }