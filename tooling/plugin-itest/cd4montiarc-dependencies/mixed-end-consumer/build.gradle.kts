/* (c) https://github.com/MontiCore/monticore */
import montiarc.tooling.cd2pojo.plugin.cd2PojoDependencyDeclarationConfigName
import montiarc.tooling.plugin.cd2pojo4MaDeclarationConfigName
import montiarc.tooling.plugin.montiarcDependencyDeclarationConfigName

buildscript {
  dependencies {
    classpath("montiarc.tooling:cd2pojo-plugin")
    classpath("montiarc.tooling:plugin")
  }
}

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("cd2pojo")
  id("montiarc")
}

group = "montiarc.tooling.plugin-itest.cd4montiarc-dependencies"
version = "7.6.0-SNAPSHOT"

tasks.getByName<Test>("test") {
  enabled = false
}

// The source set names represent how they dependend on project "c"
// (including the name of the configurations they use to declare this dependency)
// E.g., the following source set uses montiarc(...) to depend on ":ma-only-c"
val maToMixedCWith4MaDepToB: SourceSet = sourceSets.create("maToMixedCWith4MaDepToB")
val maToMixedCWithCdDepToB: SourceSet = sourceSets.create("maToMixedCWithCdDepToB")
val maToMaOnlyC: SourceSet = sourceSets.create("maToMaOnlyC")
val cd4maToMixedCWith4MaDepToB: SourceSet = sourceSets.create("cd4maToMixedCWith4MaDepToB")
val cd4maToMixedCWithCdDepToB: SourceSet = sourceSets.create("cd4maToMixedCWithCdDepToB")
val cd4maToB: SourceSet = sourceSets.create("cd4maToB")
val cdToMixedCWith4MaDepToB: SourceSet = sourceSets.create("cdToMixedCWith4MaDepToB")
val cdToMixedCWithCdDepToB: SourceSet = sourceSets.create("cdToMixedCWithCdDepToB")
val cdToB: SourceSet = sourceSets.create("cdToB")

dependencies {
  add(
    maToMixedCWith4MaDepToB.montiarcDependencyDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
  )
  add(
    maToMixedCWithCdDepToB.montiarcDependencyDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
  )
  add(
    maToMaOnlyC.montiarcDependencyDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:ma-only-c")
  )
  add(
    cd4maToMixedCWith4MaDepToB.cd2pojo4MaDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
  )
  add(
    cd4maToMixedCWithCdDepToB.cd2pojo4MaDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
  )
  add(
    cd4maToB.cd2pojo4MaDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:cd-b")
  )
  add(
    cdToMixedCWith4MaDepToB.cd2PojoDependencyDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
  )
  add(
    cdToMixedCWithCdDepToB.cd2PojoDependencyDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
  )
  add(
    cdToB.cd2PojoDependencyDeclarationConfigName,
    project(":tooling:plugin-itest:cd4montiarc-dependencies:cd-b")
  )
}

tasks.check.configure {
  dependsOn(maToMixedCWith4MaDepToB.compileJavaTaskName)
  dependsOn(maToMixedCWithCdDepToB.compileJavaTaskName)
  dependsOn(maToMaOnlyC.compileJavaTaskName)
  dependsOn(cd4maToMixedCWith4MaDepToB.compileJavaTaskName)
  dependsOn(cd4maToMixedCWithCdDepToB.compileJavaTaskName)
  dependsOn(cd4maToB.compileJavaTaskName)
  dependsOn(cdToMixedCWith4MaDepToB.compileJavaTaskName)
  dependsOn(cdToMixedCWithCdDepToB.compileJavaTaskName)
  dependsOn(cdToB.compileJavaTaskName)
}
