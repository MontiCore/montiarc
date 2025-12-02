/* (c) https://github.com/MontiCore/monticore */
import com.diffplug.spotless.generic.LicenseHeaderStep

plugins {
  java
  id("com.diffplug.spotless")
}

spotless {
  isEnforceCheck = false
  java {
    targetExclude(fileTree(layout.buildDirectory) { include("**/*.java") })

    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
    removeUnusedImports()
    endWithNewline()
  }
  kotlin {
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
