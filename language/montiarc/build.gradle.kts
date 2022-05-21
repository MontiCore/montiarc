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

  api(project(":language:core"))
  api(project(":language:compute"))
  api(project(":language:features"))
  api(project(":language:generics"))
  api(project(":language:modes"))
  api(project(":language:mode-transitions"))

  implementation(libs.monticoreClass2MC)
  implementation("${libs.guava}:${libs.guavaVersion}")
  implementation("${libs.codehausJanino}:${libs.codehausVersion}")

  testImplementation((project(":language:basis"))) {
    capabilities {
      requireCapability("montiarc.language:basis-tests")
    }
  }

  testImplementation("${libs.assertj}:${libs.assertjVersion}")
  testImplementation("${libs.mockito}:${libs.mockitoVersion}")
  testImplementation("${libs.junitAPI}:${libs.junitVersion}")
  testImplementation("${libs.junitParams}:${libs.junitVersion}")
  testRuntimeOnly("${libs.junitEngine}:${libs.junitVersion}")
}

tasks.getByName<de.monticore.MCTask>("grammar").grammar
    .set(file(project(":language").projectDir.toString() + "/grammars/MontiArc.mc4") )

sourceSets {
  main {
    java.setSrcDirs(setOf("main/java", tasks.getByName<de.monticore.MCTask>("grammar").outputDir))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.srcDirs(setOf("test/java"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}

tasks.getByName<Test>("test").useJUnitPlatform()