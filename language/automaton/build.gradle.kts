/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":language").buildDir.toString() + "/${project.name}")

dependencies {
  grammar("${libs.monticoreGrammar}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.mcGrammarsCapability) }
  }
  grammar("${libs.monticoreStatecharts}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.scGrammarsCapability) }
  }

  api(project(":language:basis"))
  api(libs.monticoreStatecharts)

  implementation("${libs.apache}:${libs.apacheVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation((project(":language:basis"))) {
    capabilities {
      requireCapability("montiarc.language:basis-tests")
    }
  }

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

tasks.register<de.monticore.MCTask>("generateArcAutomaton") {
  grammar.set(file(project(":language").projectDir.toString() + "/grammars/ArcAutomaton.mc4") )
  handcodedPath.add("$projectDir/main/java")
  modelPath.add(project(":language").projectDir.toString() + "/grammars")
  outputDir.set(file("$buildDir/sources/main/java/"))
}
sourceSets {
  main {
    java.setSrcDirs(setOf("main/java", tasks.getByName<de.monticore.MCTask>("generateArcAutomaton").outputDir))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.srcDirs(setOf("test/java"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}