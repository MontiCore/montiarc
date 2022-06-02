/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.language")
}

buildDir = file(project(":languages").buildDir.toString() + "/${project.name}")

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
    .set(file(project(":languages").projectDir.toString() + "/grammars/ArcBasis.mc4") )

tasks.getByName<Test>("test").useJUnitPlatform()

java.sourceSets["main"].java.srcDirs(tasks.getByName<de.monticore.MCTask>("grammar").outputDir)

java.registerFeature("tests") {
  usingSourceSet(sourceSets.getByName("test"))
}