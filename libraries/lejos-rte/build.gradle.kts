/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc")
}

val hwcDir = "$projectDir/main/java"
val genDirCd = "$buildDir/generated-sources/cd"

sourceSets {
  main {
    java.srcDirs(hwcDir, genDirCd)
    montiarc.srcDir("$projectDir/main/montiarc")
  }
  create("models") {
    resources.srcDirs(main.get().montiarc.srcDirs, "$projectDir/main/resources")
  }
}


java {
  registerFeature("models") {
    usingSourceSet(sourceSets["models"])
  }
}

// Configurations
val generateCD = configurations.create("generateCD")

dependencies {
  generateCD(project(":generators:cd2pojo"))

  api(project(":libraries:majava-rte"))
  api("${libs.lejos}:${libs.lejosVersion}")
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
}

val genCdTask = tasks.register<JavaExec>("generateCD") {
  classpath(generateCD)
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
tasks.compileJava { dependsOn(tasks.compileMontiarc) }
tasks.named("modelsJar") { dependsOn(tasks.compileJava) }
