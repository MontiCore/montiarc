/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

val hwcDir = "$projectDir/main/java"
val genDir = "$buildDir/generated-sources"

sourceSets["main"].java {
  srcDir(genDir)
}

// Configurations
val generateCD = configurations.create("generateCD")
val generateMA = configurations.create("generateMA")
val models = configurations.create("models")

dependencies {
  generateCD(project(":generators:cd2pojo"))
  generateMA(project(":generators:ma2java"))

  api(project(":libraries:lejos-rte"))
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")

  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")

  models(project(":libraries:lejos-rte")) {
    capabilities { requireCapability(libs.lejosModels) }
  }
}

val unpackLibModelsTask = tasks.register<Sync>("unpackmodels") {
  dependsOn(models)

  from( models.map { zipTree(it) } )
  into("$buildDir/models")
  exclude("META-INF/")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/main/resources, $buildDir/models", genDir, hwcDir)
  args("-c2mc")
  outputs.dir(genDir)
}

val genMaTask = tasks.register<JavaExec>("generateMontiArc") {
  classpath(generateMA)
  mainClass.set("montiarc.generator.MontiArcTool")

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }

  args("-mp", "$projectDir/main/resources", "$buildDir/models")
  args("-path", genDir)
  args("-o", genDir)
  args("-hwc", hwcDir)
  args("-c2mc")
  outputs.dir(genDir)
}

// Setting up task dependencies
genCdTask { dependsOn(unpackLibModelsTask) }
genMaTask { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(genMaTask) }

genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
genMaTask { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }