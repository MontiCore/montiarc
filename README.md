<!-- (c) https://github.com/MontiCore/monticore -->
# MontiArc Core Project

The MontiArc Core repository contains everything related to the common basis of the MontiArc architecture description language. This project is maintained by the [Working Group for Model-Driven Systems Engineering (MDSE)][mdse].

© https://github.com/MontiCore/monticore Contact: [David Schmalzing](https://git.rwth-aachen.de/david.schmalzing)

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

## Tool Documentation

The [MontiArcCLITool](languages/montiarc-fe/src/main/java/montiarc/cli/MontiArcCLI.java) offers capabilities for processing MontiArc component models.
It provides multiple options that can be used as follows:

`java -jar MontiArcCLI.jar [-h] -i <fileName> [-path <p>] [-pp [<file>]] [-s [<file>]]`

where the arguments are:

| Option                            | Explanation |
| ------                            | ------ |
| `-h, --help`                      | Prints the help dialog. |
| `-v, --version`                   | Prints version information. |
| `-mp, --modelpath <dirlist>`      | Sets the artifact path for the input component models, space separated. |
| `-path <dirlist>`                 | Sets the artifact path for imported symbols, space separated. |
| `-pp, --prettyprint <dir>`        | Prints the AST of the component models to stdout or the specified directory (optional). |
| `-s, --symboltable <dir>`         | Serializes and prints the symbol table to stdout or the specified output directory (optional). |

Exemplary usage:

```
  java -jar MontiArcCLI.jar -h
  java -jar MontiArcCLI.jar -mp applications/bumperbot/src/main/resources
``` 

The [MA2JavaCLITool](generators/ma2java/src/main/java/montiarc/generator/MontiArcCLI.java) extends the MontiArcCLITool with code generating capabilites. It provides the following options in addition to those defined above:

| Option                            | Explanation |
| ------                            | ------ |
| `-o, --output <dir>`              | Sets the target path for the generated files (optional). |
| `-hwc <dir>`                      | Sets the artifact path for handwritten code (optional). |

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

