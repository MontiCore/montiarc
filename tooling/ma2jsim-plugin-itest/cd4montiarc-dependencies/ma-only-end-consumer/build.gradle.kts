/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.BuildConstants
import montiarc.gradle.montiarc.cd2pojo4montiarcConfigName
import montiarc.gradle.montiarc.montiarcConfigName

plugins {
  id("montiarc.build.jvm")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")

  id("java")
  id("montiarc-jsim")
  // This project tests MontiArc plugin application without cd2pojo
}

group = "montiarc.tooling.ma2jsim-plugin-itest.cd4montiarc-dependencies"
version = BuildConstants.VERSION

tasks.getByName<Test>("test") {
  enabled = false
}

// The source set names represent how they dependend on project "c"
// (including the name of the configurations they use to declare this dependency)
// E.g., the following source set uses montiarc(...) to depend on ":ma-only-c"
val montiarcToMontiArcOnly: SourceSet = sourceSets.create("montiarcToMontiArcOnlyC")
val montiarcToMixedCWith4MontiArcDepToB: SourceSet = sourceSets.create("montiarcToMixedCWith4MontiArcDepToB")
val montiarcToMixedCWithClassDiagramDepToB: SourceSet = sourceSets.create("montiarcToMixedCWithClassDiagramDepToB")
val cd4maToMixedCWith4MontiArcDepToB: SourceSet = sourceSets.create("cd4maToMixedCWith4MontiArcDepToB")
val cd4maToMixedCWithClassDiagramDepToB: SourceSet = sourceSets.create("cd4maToMixedCWithClassDiagramDepToB")
val cd4maToB: SourceSet = sourceSets.create("cd4maToB")

dependencies {
  add(
    montiarcToMontiArcOnly.montiarcConfigName,
    project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:ma-only-c")
  )
  add(
    montiarcToMixedCWith4MontiArcDepToB.montiarcConfigName,
    project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
  )
  add(
    montiarcToMixedCWithClassDiagramDepToB.montiarcConfigName,
    project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
  )
  add(
    cd4maToMixedCWith4MontiArcDepToB.cd2pojo4montiarcConfigName,
    project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:mixed-c-with-4ma-dep-to-b")
  )
  add(
    cd4maToMixedCWithClassDiagramDepToB.cd2pojo4montiarcConfigName,
    project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:mixed-c-with-cd-dep-to-b")
  )
  add(
    cd4maToB.cd2pojo4montiarcConfigName,
    project(":tooling:ma2jsim-plugin-itest:cd4montiarc-dependencies:cd-b")
  )
}

tasks.check.configure {
  dependsOn(montiarcToMontiArcOnly.compileJavaTaskName)
  dependsOn(montiarcToMixedCWith4MontiArcDepToB.compileJavaTaskName)
  dependsOn(montiarcToMixedCWithClassDiagramDepToB.compileJavaTaskName)
  dependsOn(cd4maToMixedCWith4MontiArcDepToB.compileJavaTaskName)
  dependsOn(cd4maToMixedCWithClassDiagramDepToB.compileJavaTaskName)
  dependsOn(cd4maToB.compileJavaTaskName)
}
