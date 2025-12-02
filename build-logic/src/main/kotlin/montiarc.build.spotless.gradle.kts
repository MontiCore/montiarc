/* (c) https://github.com/MontiCore/monticore */

plugins {
  java
  id("com.diffplug.spotless")
}

spotless {
  isEnforceCheck = false
  java {
    targetExclude(fileTree(layout.buildDirectory) { include("**/*.java") })

    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
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
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */", "(\\/\\*|\\/\\/|package|import|component)")
    trimTrailingWhitespace()
    leadingTabsToSpaces(2)
    endWithNewline()
  }
  format("classDiagram") {
    target("**/*.cd")
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */", "(\\/\\*|\\/\\/|package|import|classdiagram)")
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
