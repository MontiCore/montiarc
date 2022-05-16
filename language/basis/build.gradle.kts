/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("monticore")
  id("java-library")
  id("montiarc.build.publishing")
}

val mc_version: String by project
val se_commons_version: String by project
val guava_version: String by project
val junit_jupiter_version: String by project
val antlr_version: String by project
val janino_version: String by project
val mockito_version: String by project

buildDir = file(project(":language").buildDir.toString() + "/${project.name}")

configurations {
  grammar {
    isCanBeResolved=true
    isCanBeConsumed=true
  }
}

dependencies {
  //Grammar dependencies
  grammar("de.monticore:monticore-grammar:$mc_version") {
    capabilities {
      requireCapability("de.monticore:monticore-grammar-grammars")
    }
  }

  //MontiCore dependencies
  api("de.monticore:monticore-grammar:$mc_version")

  //Other dependencies
  api("de.se_rwth.commons:se-commons-logging:$se_commons_version")
  implementation("com.google.guava:guava:$guava_version")
  implementation("org.apache.commons:commons-lang3:3.9")
  implementation("org.codehaus.janino:janino:$janino_version")
  testImplementation("org.mockito:mockito-core:$mockito_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")
}

tasks.register<de.monticore.MCTask>("generateArcBasis") {
  grammar.set(file(project(":language").projectDir.toString() + "/grammars/ArcBasis.mc4") )
  handcodedPath.add("$projectDir/main/java")
  modelPath.add(project(":language").projectDir.toString() + "/grammars")
  outputDir.set(file("$buildDir/sources/main/java/"))
}

tasks.getByName<Test>("test").useJUnitPlatform()

sourceSets {
  main {
    java.setSrcDirs(setOf("$projectDir/main/java", tasks.getByName<de.monticore.MCTask>("generateArcBasis").outputDir))
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