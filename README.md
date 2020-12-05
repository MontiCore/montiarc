<!-- (c) https://github.com/MontiCore/monticore -->
# MontiArc Core Project

The MontiArc Core repository contains everything related to the common basis of the MontiArc architecture description language. This project is maintained by the [Working Group for Model-Driven Systems Engineering (MDSE)][mdse].

Contact: @puetzer, @david.schmalzing, @wortmann

[se-rwth]: http://www.se-rwth.de
[mdse]:http://www.se-rwth.de/teams/mdse/

## The MontiArc Architecture Description Language

<img src="pics/elevatorExample.PNG" alt="drawing" height="400px"/>

In MontiArc, architectures are described as component and connector systems in which autonomously acting components perform computations. Communication between components is regulated by connectors between the components’ interfaces, which are stable and build up by typed, directed ports. Components are either atomic or composed of connected subcomponents. Atomic components yield behavior descriptions in the form of embedded time-synchronous port automata, or via integration of handcrafted code. For composed components the behavior emerges from the behavior of their subcomponents. 

## Project Structure

* languages/
  * dynamic-fe
  * montiarc-fe
* generators/
    * cd2pojo
    * dynma2java
    * maa2java-xtend
* applications/
    * bumperbot  
* libraries/
    * maa-rte
    * lejos-rte
    * simulator-rte
    * maJavaLib

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

