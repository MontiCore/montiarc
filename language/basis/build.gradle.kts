/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":language").buildDir.toString() + "/${project.name}")

configurations {
  grammar {
    isCanBeResolved=true
    isCanBeConsumed=true
  }
}

dependencies {
  grammar("${libs.monticoreGrammar}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.mcGrammarsCapability) }
  }

  api(platform(project(":base-platform")))
  api(libs.monticoreGrammar)
  api(libs.seCommonsLogging)

  implementation("${libs.apache}:${libs.apacheVersion}")
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}


tasks.getByName<de.monticore.MCTask>("grammar").grammar
    .set(file(project(":language").projectDir.toString() + "/grammars/ArcBasis.mc4") )

tasks.getByName<Test>("test").useJUnitPlatform()

sourceSets {
  main {
    java.setSrcDirs(setOf("$projectDir/main/java", tasks.getByName<de.monticore.MCTask>("grammar").outputDir))
    resources.setSrcDirs(setOf("$projectDir/main/resources"))
  }
  test {
    java.srcDirs(setOf("$projectDir/test/java"))
    resources.setSrcDirs(setOf("$projectDir/test/resources"))
  }
}

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}