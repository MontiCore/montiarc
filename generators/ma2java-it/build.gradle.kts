/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.integration-test")
  id("montiarc")
}

val hwcDir = "$projectDir/main/java"
val genDirCd = "$buildDir/generated-sources/cd"

sourceSets {
  main {
    java.srcDir("$genDirCd")
    montiarc.srcDir("$projectDir/main/montiarc")
  }
}

val generateCD = configurations.create("generateCD")

dependencies {
  generateCD(project(":generators:cd2pojo"))

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

  args("$projectDir/main/resources", genDirCd, hwcDir)
  outputs.dir(genDirCd)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileMontiarc {
  symbolImportDir.from(genDirCd)
  useClass2Mc.set(true)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.compileMontiarc { dependsOn(genCdTask) }
tasks.compileMontiarc { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }
genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }