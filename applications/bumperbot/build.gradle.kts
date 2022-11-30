/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc")
}

val hwcDir = "$projectDir/main/java"
val genDirCd = "$buildDir/generated-sources/cd"
val unpackedModelsDir = "$buildDir/models"

sourceSets {
  main {
    java.srcDir(genDirCd)
    montiarc.srcDirs("$projectDir/main/montiarc", "$buildDir/models")
  }
}

// Configurations
val generateCD = configurations.create("generateCD")
val models = configurations.create("models")

dependencies {
  generateCD(project(":generators:cd2pojo"))

  api(project(":libraries:lejos-rte"))
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")

  models(project(":libraries:lejos-rte")) {
    capabilities { requireCapability(libs.lejosModels) }
  }
}

val unpackLibModelsTask = tasks.register<Sync>("unpackmodels") {
  dependsOn(models)

  from( models.map { zipTree(it) } )
  into(unpackedModelsDir)
  exclude("META-INF/")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
  mainClass.set("de.monticore.cd2pojo.POJOGeneratorScript")

  args("$projectDir/main/resources, $unpackedModelsDir", genDirCd, hwcDir)
  args("-c2mc")
  outputs.dir(genDirCd)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileMontiarc {
  symbolImportDir.from(genDirCd, unpackedModelsDir)
  useClass2Mc.set(true)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

// Setting up task dependencies
genCdTask { dependsOn(unpackLibModelsTask) }
tasks.compileMontiarc { dependsOn(genCdTask) }
tasks.compileJava { dependsOn(tasks.compileMontiarc) }

genCdTask { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
tasks.compileMontiarc { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }