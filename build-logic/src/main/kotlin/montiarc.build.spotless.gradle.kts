/* (c) https://github.com/MontiCore/monticore */
import com.diffplug.spotless.generic.LicenseHeaderStep

plugins {
  java
  id("com.diffplug.spotless")
}

fun sourcesOutsideBuildDirectory(sources: (SourceSet) -> SourceDirectorySet, vararg includes: String) = provider {
  sourceSets.flatMap { sources(it).srcDirs }
    .filterNot { it.startsWith(layout.buildDirectory.get().asFile) }
    .map { fileTree(it) { include(*includes) } }
}

spotless {
  isEnforceCheck = false
  java {
    target(sourcesOutsideBuildDirectory(SourceSet::getAllJava, "**/*.java"))

    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
    removeUnusedImports()
    endWithNewline()
  }
  kotlin {
    target(sourcesOutsideBuildDirectory(SourceSet::getAllSource, "**/*.kt", "**/*.kts"))
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
    endWithNewline()
  }
  kotlinGradle {
    endWithNewline()
  }
  format("montiArc") {
    target("**/*.arc")
    targetExclude("**main/montiarc/automata/Comments.arc")
    addStep(LicenseHeaderStep.headerDelimiter(
      "/* (c) https://github.com/MontiCore/monticore */\n", "package|import"
    ).withName("LicenseHeaderWithPackageOrImport")
      .withContentPattern("(?s)^(?=.*(?m)(?:^package.*;$|^import.*;$)).+$").build())
    addStep(LicenseHeaderStep.headerDelimiter(
      "/* (c) https://github.com/MontiCore/monticore */\n\n", "/\\*(?! \\(c\\))|component|<<"
    ).withName("LicenseHeaderWithoutPackageOrImport")
      .withContentPattern("(?s)^(?!.*(?m)(?:^package.*;$|^import.*;$)).+$").build())
    leadingTabsToSpaces(2)
    endWithNewline()
  }
  format("classDiagram") {
    target("**/*.cd")
    trimTrailingWhitespace()
    leadingTabsToSpaces(2)
    endWithNewline()
  }
  format("markdown") {
    target("docs/**/*.md", "README.md")
    endWithNewline()
  }
  format("montiCore") {
    target("**/*.mc4")
    leadingTabsToSpaces(2)
    endWithNewline()
  }
}
