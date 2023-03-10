/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.java-library")
  id("montiarc")
}

val cdModelDir = "$projectDir/main/resources"
val cdHWCDir = "$projectDir/main/java"
val cdGenJavaDir = "$buildDir/cd/java"
val cdGenSymDir = "$buildDir/cd/sym"

sourceSets {
  main {
    java {
      srcDir(cdGenJavaDir)
    }
    montiarc {
      setSrcDirs(setOf("$projectDir/main/montiarc"))
    }
  }
}

val cd4a: Configuration = configurations.create("cd4a")

dependencies {
  cd4a(project(":generators:cd2pojo"))

  api(project(":libraries:majava-rte"))
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")
  implementation("${libs.seCommonsLogging}:${libs.monticoreVersion}")
  implementation("${libs.seCommonsUtils}:${libs.monticoreVersion}")
}

val compileCD4A = tasks.register<JavaExec>("compileCD4A") {
  classpath(cd4a)
  mainClass.set("de.monticore.cd2pojo.CD2PojoTool")

  args("-i", cdModelDir)
  args("-c")
  args("-c2mc")
  args("-gen")
  args("-o", cdGenJavaDir)
  args("-s", cdGenSymDir)
  args("-hwc", cdHWCDir)
  inputs.dir(cdModelDir)
  outputs.dirs(cdGenJavaDir, cdGenSymDir)
}

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.compileMontiarc {
  symbolImportDir.from(cdGenSymDir)
  useClass2Mc.set(true)

  val enableAttachDebugger = false
  if(enableAttachDebugger) {
    jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,address=5005,suspend=y")
  }
}

tasks.compileMontiarc { dependsOn(compileCD4A) }

compileCD4A { mustRunAfter(project(":generators:cd2pojo").tasks.withType(Test::class)) }
tasks.compileMontiarc { mustRunAfter(project(":generators:ma2java").tasks.withType(Test::class)) }
