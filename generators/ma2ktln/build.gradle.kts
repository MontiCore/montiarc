/* (c) https://github.com/MontiCore/monticore */
val cd4a_version : String by project
val mc_version : String by project
val se_commons_version : String by project
val testmodels_classifier : String by project
val janino_version : String by project
val groovy_version : String by project
val java_formatter_version : String by project
val java_parser_version : String by project
val emf_version : String by project
val eclipse_compiler_version : String by project
val mockito_version : String by project
val junit_jupiter_version : String by project

plugins {
  java
  jacoco
  id("com.github.johnrengelman.shadow")
  id("montiarc.build.java-library")
}

group = "montiarc.generators"

tasks.test {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

configurations { create("testmodels") }

configurations.all {
  resolutionStrategy {
    force("de.monticore.lang:cd4analysis:$cd4a_version")
  }
}

dependencies {
  // MontiCore dependencies
  implementation("de.monticore:monticore-grammar:$mc_version")
  implementation("de.monticore.lang:statecharts:$mc_version") {
    exclude("de.monticore.lang", "cd4analysis")
  }
  implementation("de.se_rwth.commons:se-commons-groovy:$se_commons_version")

  // dependency for the tool
  implementation(project(":generators:ma2java"))

  // Internal dependencies
  implementation(project(":language:montiarc"))
  implementation(project(":libraries:majava-rte"))
  testImplementation(project(":generators:cd2pojo"))

  // Model dependencies
  configurations["testmodels"]("montiarc.languages:montiarc-fe:$version:$testmodels_classifier")

  // Other dependencies
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
  dependsOn(configurations["testmodels"])

  from(configurations["testmodels"].map { zipTree(it) })
  into("$buildDir/test-models")
}

java {
  //withJavadocJar()
  withSourcesJar()
}

// All in one cli-jar
tasks.shadowJar {
  manifest {
    attributes["Main-Class"] = "montiarc.kotlin.generator.ModeArcTool"
  }
  isZip64 = true
  archiveClassifier.set("cli")
  archiveBaseName.set("MontiArc2Kotlin")
  archiveFileName.set("${archiveBaseName.get()}CLI-${archiveVersion.get()}.${archiveExtension.get()}")
}

tasks.jar { dependsOn(tasks.shadowJar) }