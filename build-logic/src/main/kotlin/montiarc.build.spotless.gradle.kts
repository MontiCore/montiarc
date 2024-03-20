/* (c) https://github.com/MontiCore/monticore */

plugins {
  java
  id("com.diffplug.spotless")
}

spotless {
  isEnforceCheck = false
  java {
    targetExclude(fileTree("$buildDir") { include("**/*.java") })

    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
  }
  kotlin {
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
  }
  kotlinGradle {
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */", ".")
  }
  format("montiArc") {
    target("**/*.arc")
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */", ".")
    trimTrailingWhitespace()
    indentWithSpaces(2)
    endWithNewline()
  }
  format("markdown") {
    target("**/*.md")
    licenseHeader("<!-- (c) https://github.com/MontiCore/monticore -->", ".")
    endWithNewline()
  }
  format("montiCore") {
    target("**/*.mc4")
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */", ".")
    indentWithSpaces(2)
    endWithNewline()
  }
}