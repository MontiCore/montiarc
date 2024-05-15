/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.BuildConstants
import montiarc.gradle.montiarc.cd2pojo4MaDeclarationConfigName
import montiarc.gradle.montiarc.montiarcDependencyDeclarationConfigName

plugins {
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc")
  // This project tests MontiArc plugin application without cd2pojo
}

group = "montiarc.tooling.ma2java-plugin-itest.cd4montiarc-dependencies"
version = BuildConstants.VERSION

montiarc {
  internalMontiArcTesting.set(true)
}

tasks.getByName<Test>("test") {
  enabled = false
}

// The source set names represent how they dependend on project "c"
// (including the name of the configurations they use to declare this dependency)
// E.g., the following source set uses montiarc(...) to depend on ":ma-only-c"
val maToMaOnlyC: SourceSet = sourceSets.create("maToMaOnlyC")
val maToMixedCWith4MaDepToB: SourceSet = sourceSets.create("maToMixedCWith4MaDepToB")
val maToMixedCWithCdDepToB: SourceSet = sourceSets.create("maToMixedCWithCdDepToB")
val cd4maToMixedCWith4MaDepToB: SourceSet = sourceSets.create("cd4maToMixedCWith4MaDepToB")
val cd4maToMixedCWithCdDepToB: SourceSet = sourceSets.create("cd4maToMixedCWithCdDepToB")
val cd4maToB: SourceSet = sourceSets.create("cd4maToB")

dependencies {
  add(
    maToMaOnlyC.montiarcDependencyDeclarationConfigName,
    project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:ma-only-c")
  )
  add(
    maToMixedCWith4MaDepToB.montiarcDependencyDeclarationConfigName,
    project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
  )
  add(
    maToMixedCWithCdDepToB.montiarcDependencyDeclarationConfigName,
    project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
  )
  add(
    cd4maToMixedCWith4MaDepToB.cd2pojo4MaDeclarationConfigName,
    project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
  )
  add(
    cd4maToMixedCWithCdDepToB.cd2pojo4MaDeclarationConfigName,
    project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
  )
  add(
    cd4maToB.cd2pojo4MaDeclarationConfigName,
    project(":tooling:ma2java-plugin-itest:cd4montiarc-dependencies:cd-b")
  )
}

tasks.check.configure {
  dependsOn(maToMaOnlyC.compileJavaTaskName)
  dependsOn(maToMixedCWith4MaDepToB.compileJavaTaskName)
  dependsOn(maToMixedCWithCdDepToB.compileJavaTaskName)
  dependsOn(cd4maToMixedCWith4MaDepToB.compileJavaTaskName)
  dependsOn(cd4maToMixedCWithCdDepToB.compileJavaTaskName)
  dependsOn(cd4maToB.compileJavaTaskName)
}
