<!-- (c) https://github.com/MontiCore/monticore -->
# MontiArc

The MontiArc Core repository contains everything related to the common basis
of the MontiArc architecture description language. This project is maintained
by the [Working Group for Model-Driven Systems Engineering (MDSE)][mdse].

[se-rwth]: http://www.se-rwth.de
[mdse]:http://www.se-rwth.de/teams/mdse/

The language for MontiArc Architecture diagrams is split up into 4 languages:
- **ArchitectureBasis**: basic language component for architectures consisting
of ports, components, and connectors
- **Statechart4MA**: basic language component for component behavior description
using automata, states, and transitions
- **ComfortableArc**: extension of the architecture basis with comfort elements
to ease the description of architectures
- **MontiArc**: language for description of components with embedded behavior 
combining `ArchitectureBasis`, `Statechart4MA`, and `ComfortableArc`.

## The MontiArc Architecture Description Language

In MontiArc, architectures are described as component and connector systems in
which autonomously acting components perform computations. Communication between
components is regulated by connectors between the components’ interface,
which are stable and build up by typed, directed ports. Components are either 
atomic or composed of connected subcomponents. Atomic components yield
behavior descriptions in the form of embedded time-synchronous port automata, 
embedded JavaDSL models, or via integration of handcrafted code. For composed 
components the behavior emerges from the behavior of their subcomponents. 

The grammar file is [`MontiArc`][MontiArcGrammar].

[MontiArcGrammar]: https://git.rwth-aachen.de/monticore/montiarc/core/-/blob/modularization/languages/montiarc-fe/src/main/grammars/MontiArc.mc4

## Handwritten Extensions
### AST

### Symbols

## Functionality
### CoCos
The CoCos can be found in 
 [`montiarc.cocos`][CoCosPackage] and are combined accessible in
 [`montiarc.cocos.MontiArcCoCos`][MontiArcCoCos].
 
[CoCosPackage]: https://git.rwth-aachen.de/monticore/montiarc/core/-/tree/modularization/languages/montiarc-fe/src/main/java/montiarc/cocos
[MontiArcCoCos]: https://git.rwth-aachen.de/monticore/montiarc/core/-/blob/modularization/languages/montiarc-fe/src/main/java/montiarc/cocos/MontiArcCoCos.java

The context conditions check different parts of the models, to ensure the
 semantic correctness, here is a list of some of the important ones:
- Uniqueness of names of e.g. components, ports, fields, parameters
- Type compatibility for connected ports
- ...

### Transformations

### PrettyPrinter


# copyright

[(c) see MontiCore 3 Level License](https://github.com/MontiCore/monticore)