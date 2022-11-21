/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

abstract class Modules {

  val apacheBcelVersion = "6.0"
  val apacheCommonsVersion = "3.9"
  val apacheGroovyVersion = "4.0.2"
  val assertjVersion = "3.7.0"
  val codehausVersion = "3.0.7"
  val formatVersion = "1.7"
  val emfVersion = "1.2.0"
  val groovyVersion = "3.0.7"
  val guavaVersion = "30.1-jre"
  val javaParserVersion = "3.18.0"
  val junitVersion = "5.8.0"
  val kotlinVersion = "1.6.0"
  val kotlinxVersion = "1.6.0"
  val lejosVersion = "0.9.1-beta"
  val logbackVersion = "1.2.6"
  val mockitoVersion = "3.2.0"
  val monticoreVersion = "7.5.0-SNAPSHOT"
  val monticoreLSPVersion = "7.5.0-SNAPSHOT"

  val apache = "org.apache.commons:commons-lang3"
  val apacheBcel = "org.apache.bcel:bcel"
  val apacheGroovy = "org.apache.groovy:groovy"
  val assertj = "org.assertj:assertj-core"
  val codehausJanino = "org.codehaus.janino:janino"
  val format = "com.google.googlejavaformat:google-java-format"
  val guava = "com.google.guava:guava"
  val javaParser = "com.github.javaparser:javaparser-core"
  val junitAPI = "org.junit.jupiter:junit-jupiter-api"
  val junitEngine = "org.junit.jupiter:junit-jupiter-engine"
  val junitParams = "org.junit.jupiter:junit-jupiter-params"
  val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core"
  val kotlinJunit = "org.jetbrains.kotlin:kotlin-test-junit"
  val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
  val lejos = "lejos.nxt:classes"
  val logbackCore = "ch.qos.logback:logback-core"
  val logbackClassic = "ch.qos.logback:logback-classic"
  val mockito = "org.mockito:mockito-core"
  val monticoreCD4Analysis = "de.monticore.lang:cd4analysis"
  val monticoreClass2MC = "de.monticore:class2mc"
  val monticoreGrammar = "de.monticore:monticore-grammar"
  val monticoreRuntime = "de.monticore:monticore-runtime"
  val monticoreOCL = "de.monticore.lang:ocl"
  val monticoreStatecharts = "de.monticore.lang:statecharts"
  val seCommonsGroovy = "de.se_rwth.commons:se-commons-groovy"
  val seCommonsLogging = "de.se_rwth.commons:se-commons-logging"
  val seCommonsUtils = "de.se_rwth.commons:se-commons-utilities"

  val lejosModels = "montiarc.libraries:lejos-rte-models"
  val dynsimRteModel = "montiarc.libraries:dynsim-reaction-rte-model"
  val mcGrammarsCapability = "de.monticore:monticore-grammar-grammars"
  val oclGrammarsCapability = "de.monticore.lang:ocl-grammars"
  val scGrammarsCapability = "de.monticore.lang:statecharts-grammars"
}