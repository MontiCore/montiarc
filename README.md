<!-- (c) https://github.com/MontiCore/monticore -->
# The MontiArc Architecture Description Language

In MontiArc, architectures are described as component and connector systems in which autonomously acting components 
perform computations. Communication between components is regulated by connectors between the components’ interfaces, 
which are stable and built up by typed, directed ports. Components are either atomic or composed of connected 
subcomponents. Atomic components yield behavior descriptions in the form of embedded time-synchronous port automata 
or via integration of handcrafted code. For composed components, the behavior emerges from the behavior of their 
subcomponents. 

© https://github.com/MontiCore/monticore

## Native installation

### Prerequisites 
- Git (for checking out the project)
- Gradle 7.5.0 (for building the project)
- Java 11 (for building and executing the project)

### Installation

```
git clone <link to this Git repository>
```
Then enter the newly created folder and do 
```
gradle build
```

This should build the project. Note, building the project for the first time may
take a while. Subsequent builds should be faster.

Once the project is built, you can look at the generated source code. 
The `languages` folder contains the language components of MontiArc, more 
specifically, their frontend implementation. That is parsers to create abstract
syntax trees (ASTs) from textual models, infrastructure to create the symbol 
table, context condition checks, transformations, visitors, and a command line 
tool that puts everything together.

The `generators` folder contains code generators that translate MontiArc component 
models to some general-purpose language. Currently, available are the MontiArc to 
Java (MA2Java) and the CD to Java (CD2Pojo, Pojo for Plain Old Java) generators.

The `applications` folder contains some example applications. 
Each of them should contain a `build/generated-sources` subdirectory after
building their respective project. 

Please note that `gradle build` on the topmost folder builds the whole project. 
However, each subproject contains an individual build file. 
Executing the build command on a subproject builds everything needed for that 
project and then builds the project. See the [Gradle Website](https://gradle.org/) 
for more information about the gradle build tool.

## Building and Running Your First Application

This section guides you through building and executing your first application.
We will use the example under `application/bumperbot`.
It consists of only a few components but should showcase the build process.

We support building an application via an IDE

### Building and Running an Application using an IDE

As MontiArc generates Java code, you can use the same IDE to build both MontiArc 
and MontiArc applications.
The whole build process can be handled by gradle. 
That is, `gradle build` not only generates Java code from MontiArc component 
models, but also compiles the handwritten and generated
code, and executes tests. 
Extending the build process to construct an executable only requires defining the 
main class and setting up the build process to generate an executable.

## Tool Documentation

The MontiArcTool offers capabilities for processing MontiArc component models.
It provides multiple options that can be used as follows:

`java -jar MontiArc.jar [-h] -i <fileName> [-path <p>] [-pp [<file>]] [-s [<file>]]`

where the arguments are:

| Option                            | Explanation                                                                                    |
|-----------------------------------|------------------------------------------------------------------------------------------------|
| `-h, --help`                      | Prints the help dialog.                                                                        |
| `-v, --version`                   | Prints version information.                                                                    |
| `-mp, --modelpath <dirlist>`      | Sets the artifact path for the input component models, space separated.                        |
| `-path <dirlist>`                 | Sets the artifact path for imported symbols, space separated.                                  |
| `-pp, --prettyprint <dir>`        | Prints the AST of the component models to stdout or the specified directory (optional).        |
| `-s, --symboltable <dir>`         | Serializes and prints the symbol table to stdout or the specified output directory (optional). |

Exemplary usage:

```
  java -jar MontiArc.jar -h
  java -jar MontiArc.jar -mp application/bumperbot/main/resources
``` 

The MA2JavaTool extends the MontiArcTool with code generating capabilities. It provides the following options in addition to those defined above:

| Option                             | Explanation                                              |
|------------------------------------|----------------------------------------------------------|
| `-o, --output <dir>`               | Sets the target path for the generated files (optional). |
| `-hwc <dir>`                       | Sets the artifact path for handwritten code (optional).  |

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/HEAD/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/HEAD/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/HEAD/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/HEAD/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

