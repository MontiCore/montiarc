/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("java-library")
  id("monticore")
  id("montiarc.build.publishing")
}

val mc_version: String by project
val se_commons_version: String by project
val guava_version: String by project
val junit_jupiter_version: String by project
val antlr_version: String by project
val janino_version: String by project
val mockito_version: String by project
val grammars_classifier: String by project
val assertj_version: String by project

buildDir = file(project(":language").buildDir.toString() + "/${project.name}")

dependencies {
  //Grammar dependencies
  grammar("de.monticore:monticore-grammar:$mc_version") {
    capabilities {
      requireCapability("de.monticore:monticore-grammar-grammars")
    }
  }
  grammar("de.monticore.lang:statecharts:$mc_version") {
    capabilities {
      requireCapability("de.monticore.lang:statecharts-$grammars_classifier")
    }
  }

  //MontiCore dependencies
  api(project(":language:core"))
  api(project(":language:compute"))
  api(project(":language:features"))
  api(project(":language:generics"))
  api(project(":language:modes"))

  //Other dependencies
  implementation("de.monticore:class2mc:$mc_version")
  implementation("com.google.guava:guava:$guava_version")
  implementation("org.codehaus.janino:janino:$janino_version")
  testImplementation((project(":language:basis"))) {
    capabilities {
      requireCapability("montiarc.language:basis-tests")
    }
  }
  testImplementation("org.assertj:assertj-core:$assertj_version")
  testImplementation("org.mockito:mockito-core:$mockito_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_jupiter_version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_jupiter_version")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_jupiter_version")
}

tasks.register<de.monticore.MCTask>("generateMontiArc") {
  grammar.set(file(project(":language").projectDir.toString() + "/grammars/MontiArc.mc4") )
  handcodedPath.add("$projectDir/main/java")
  modelPath.add(project(":language").projectDir.toString() + "/grammars")
  outputDir.set(file("$buildDir/sources/main/java/"))
}

sourceSets {
  main {
    java.setSrcDirs(setOf("main/java", tasks.getByName<de.monticore.MCTask>("generateMontiArc").outputDir))
    resources.setSrcDirs(setOf("main/resources"))
  }
  test {
    java.srcDirs(setOf("test/java"))
    resources.setSrcDirs(setOf("test/resources"))
  }
}

tasks.getByName<Test>("test").useJUnitPlatform()