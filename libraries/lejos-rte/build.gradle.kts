/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
}

val hwcDir = "$projectDir/main/java"
val genDir = "$buildDir/generated-sources"

sourceSets {
  main {
    java.srcDirs(hwcDir, genDir)
  }
  create("models") {
    resources.srcDir("$projectDir/main/resources")
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
  generateCD(project(":generators:cd2pojo"))
  generateMA(project(":generators:ma2java"))

  api(project(":libraries:majava-rte"))
  api("${libs.lejos}:${libs.lejosVersion}")
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/main/resources", genDir, hwcDir)
  outputs.dir(genDir)
}

val genMaTask = tasks.register<JavaExec>("generateMontiArc") {
  classpath(generateMA)
  mainClass.set("montiarc.generator.MontiArcTool")


  args("-mp", "$projectDir/main/resources")
  args("-path", genDir)
  args("-o", genDir)
  args("-hwc", hwcDir)
  args("-c2mc")
  outputs.dir(genDir)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

genMaTask { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(genMaTask) }
tasks.named("modelsJar") { dependsOn(tasks.compileJava) }
