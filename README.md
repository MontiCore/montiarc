# MontiArc and MontiArcAutomaton

The core repository contains everything related to the common MA and MAA
tool-chain. This is:

* languages/
  * common
  * montiarc-fe
        
        Contains MontiArcLanguage and MontiArcLanguageFamily
        
  * montiarcautomaton-fe
        
        Contains MontiArcAutomatonLanguageFamily
        
  * montiarc-behavior-fe
  * io-automata-fe
        
        Contains IOAutomatonLanguage
        
  * ...
* generators/
  * ma2java
  * maa2java
  * ...
* libraries/
  * maa-rte
  * lejos-rte
  * simulator-rte
* applications/
  * ma-bumperbot
        
        Example using MontiArcLanguage and ma2java
        
  * maa-bumperbot
        
        MAA Example using MontiArcAutomatonLanguageFamily and maa2java
        
  * ma-simulator
        
        Example setup to execute ma2java and running the simulator
        

## Frontend / Languages

A common usage scenario is aggregating MontiArc with class diagrams.
Thus, the MA frontend provides both, the plain MontiArc language and a
language family that contains MontiArc as well as class diagrams.

The plain MontiArc language is defined in MontiArcLanguage. It does not
contain class diagrams.

The language family MontiArcLanguageFamily aggregates MontiArcLanguage,
CD4AnalysisLanguage and JavaDSLLanguage.


# copyright to be added soon.

