/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

abstract class Modules {

  val apacheCommonsVersion = "3.12.0"
  val codehausVersion = "3.1.9"
  val formatVersion = "1.7"
  val guavaVersion = "31.1-jre"
  val javaSmtVersion = "3.12.0"
  val mockitoVersion = "4.9.0"
  val monticoreVersion = "7.5.0-SNAPSHOT"
  val monticoreLSPVersion = "7.5.0-SNAPSHOT"
  val z3TurnkeyVersion = "4.11.2"

  val javaSmt = "org.sosy-lab:java-smt"
  val apache = "org.apache.commons:commons-lang3"
  val codehausJanino = "org.codehaus.janino:janino"
  val format = "com.google.googlejavaformat:google-java-format"
  val guava = "com.google.guava:guava"
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
  val cd4analysis = "de.monticore.lang:cd4analysis"
  val fdLang = "de.monticore.lang:fd-lang"
  val z3Turnkey = "tools.aqua:z3-turnkey"

  val mcGrammarsCapability = "de.monticore:monticore-grammar-grammars"
  val cd4aGrammarsCapability = "de.monticore.lang:cd4analysis-grammars"
  val oclGrammarsCapability = "de.monticore.lang:ocl-grammars"
  val scGrammarsCapability = "de.monticore.lang:statecharts-grammars"
}
