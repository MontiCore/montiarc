/* (c) https://github.com/MontiCore/monticore */

plugins {
  java
  id("com.diffplug.spotless")
}

spotless {
  java {
    targetExclude(fileTree("$buildDir") { include("**/*.java") })

    // apply a specific flavor of google-java-format
    //googleJavaFormat("1.8").reflowLongStrings()
    // fix formatting of type annotations
    //formatAnnotations()
    // make sure every file has the following copyright header.
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
  }
  kotlin {
    //targetExclude("**/build/**")

    //ktfmt()    // has its own section below
    //ktlint()   // has its own section below
    //diktat()   // has its own section below
    //prettier() // has its own section below
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */")
  }
  kotlinGradle {
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */", ".")
  }
  format("montiArc") {
    target("**/*.arc")
    licenseHeader("/* (c) https://github.com/MontiCore/monticore */", "(package|import|component)")
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