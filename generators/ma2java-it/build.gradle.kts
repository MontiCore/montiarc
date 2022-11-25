/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.integration-test")
}

val hwcDir = "$projectDir/main/java"
val genDir = "$buildDir/generated-sources"

sourceSets {
  main {
    java.srcDir("$genDir")
  }
}

val generateCD = configurations.create("generateCD")
val generateMA = configurations.create("generateMA")

dependencies {
  generateCD(project(":generators:cd2pojo"))
  generateMA(project(":generators:ma2java"))

  implementation(project(":languages:montiarc"))
  implementation(project(":libraries:majava-rte"))
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath = configurations["generateCD"]
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/main/resources", genDir, hwcDir)
  outputs.dir(genDir)
}

val genMaTask = tasks.register<JavaExec>("generateMontiArc") {
  classpath(configurations["generateMA"])
  mainClass.set("montiarc.generator.MontiArcTool")

  args("-mp", "$projectDir/main/resources")
  args("-path", genDir)
  args("-o", genDir)
  args("-hwc", hwcDir)
  args("-c2mc")
  outputs.dir(genDir)
}

genMaTask { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(genMaTask) }

genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
genMaTask { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }