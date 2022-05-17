/* (c) https://github.com/MontiCore/monticore */

val mc_version: String by project
val se_commons_version: String by project
val janino_version: String by project
val groovy_version: String by project
val cd4a_version: String by project
val junit_jupiter_version: String by project
val emf_version: String by project
val java_parser_version: String by project
val java_formatter_version: String by project
val eclipse_compiler_version: String by project
val mockito_version: String by project
val testmodels_classifier: String by project

plugins {
  java
  jacoco
  id("com.github.johnrengelman.shadow")
  id("montiarc.build.java-library")
}

group = "montiarc.generators"

tasks.named<Test>("test") {
  useJUnitPlatform()
  finalizedBy(tasks.named("jacocoTestReport"))
}

configurations {
  runtimeClasspath {
    exclude("de.monticore.lang", "cd4analysis")
    exclude("org.codehaus.groovy", "groovy")
  }
  create("testmodels")
}

configurations.all {
  resolutionStrategy {
    force("de.monticore.lang:cd4analysis:$cd4a_version")
  }
}

dependencies {
  val testmodels = configurations["testmodels"]

  //MontiCore dependencies
  implementation("de.monticore:monticore-grammar:$mc_version")
  implementation("de.monticore.lang:statecharts:$mc_version")
  implementation("de.se_rwth.commons:se-commons-groovy:$se_commons_version")

  //Internal dependencies
  implementation(project(":language:montiarc"))
  implementation(project(":libraries:majava-rte"))
  testImplementation(project(":generators:cd2pojo"))

  //Model dependencies
  testmodels("montiarc.languages:montiarc-fe:$version:$testmodels_classifier")

  //Other dependencies
  implementation("org.apache.commons:commons-lang3:3.9")
  implementation("org.codehaus.janino:janino:$janino_version")
  implementation("org.codehaus.groovy:groovy:$groovy_version")
  implementation("com.google.googlejavaformat:google-java-format:$java_formatter_version")
  testImplementation("com.github.javaparser:javaparser-symbol-solver-core:$java_parser_version")
  testImplementation("org.eclipse.emf:org.eclipse.emf.compare:$emf_version")
  testImplementation("org.eclipse.emf:org.eclipse.emf.compare.match:$emf_version")
  testImplementation("org.eclipse.emf:org.eclipse.emf.compare.diff:$emf_version")
  testImplementation("org.eclipse.jdt:org.eclipse.jdt.compiler.tool:$eclipse_compiler_version")
  testImplementation("org.eclipse.jdt:org.eclipse.jdt.compiler.apt:1.3.1300")
  testImplementation("org.mockito:mockito-core:$mockito_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")
}

tasks.register<Sync>("unpackTestmodels") {
  val testModelConfig = configurations.named("testmodels")
  dependsOn(testModelConfig)

  from(testModelConfig.map { zipTree(it) } )
  into("$buildDir/test-models")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

// All in one tool-jar
tasks.shadowJar {
  minimize()
  manifest {
    attributes["Main-Class"] = "montiarc.generator.MontiArcTool"
  }
  isZip64 = true
  archiveClassifier.set("mc-tool")
  archiveBaseName.set("MontiArc2Java")
  archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

tasks.jar { dependsOn(tasks.shadowJar) }