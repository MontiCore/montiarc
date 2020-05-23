<!-- (c) https://github.com/MontiCore/monticore -->
# MontiArc - Language for Modelling Architecture

MontiArc is an architecture modeling language and framework
that provides a platform independent structure 
modeling language. It is **extensible** in several dimensions:
1. **behavioral modelling** languages, such as statecharts can be easily embedded
2. external **types**, e.g. defined using diagrams can be imported
3. the **code generation** framework can be adapted and extended for various
   target platforms.
The MontiArc language covers **components** their **ports**, **connectors** between
components and is shipped with 
embedded statecharts for component behavior description as a standard extension.

The MontiArc Core repository contains the common basis
of the MontiArc architecture description language. This project is maintained
by the [Working Group for Model-Driven Systems Engineering (MDSE)][mdse].

[se-rwth]: http://www.se-rwth.de
[mdse]:http://www.se-rwth.de/teams/mdse/

The language for MontiArc Architecture diagrams is composed from five grammars,
which also might be used independently:
- **ArcBasis**: basic language component for architectures consisting
of ports, components, and connectors
- **ComfortableArc**: extension of the architecture basis with comfort elements
to ease the description of architectures
- **GenericArc**: extension of the architecture basis for generic component types
- **Statechart4MA**: this extension to MontiArc contains 
s StateChart variant allowing component behavior description
using states, transitions, messages as stimuli, conditions and Java-like actions.
- **MontiArc**: complete language aggregates the above mentioned language 
  components and includes powerful expression and type sub-grammar.

The main grammar file is [`MontiArc`][MontiArcGrammar].

[MontiArcGrammar]: https://git.rwth-aachen.de/monticore/montiarc/core/-/blob/modularization/languages/montiarc-fe/src/main/grammars/MontiArc.mc4


## The MontiArc Architecture Description Language

In MontiArc, architectures are topologies of component and connectors in which 
autonomously acting components perform computations. Connectors define the
interaction between the components' interfaces, which consist of typed, directed
ports. Components are either atomic or composed of connected subcomponents.
Atomic components yield behavior descriptions in the form of embedded 
time-synchronous port automata, embedded JavaDSL models, or via integration of
handcrafted code. For composed components, the behavior emerges from the 
behavior of their subcomponents.

```
component InteriorLight {
  port in Boolean lightSignal,
       in Boolean doorSignal
       out OnOff status;
  ORGate or;
  lightSignal -> or.a;
  doorSignal -> or.b;
  or.c -> cntr.signal;
  component LightController cntr {
    port in OnOff signal,
         out OnOff status;
    statechart {
      initial state Off / {status = OFF};
      state On;
      Off -> On [ signal == true ] / {status = ON}
      On -> Off [ signal == false ] / {status = OFF}
    }
  }
  cntr.status -> status;
}
```
Example component of a car `InteriorLight` that turns on if a user manually 
presses a light switch or the car door opens. The  `InteriorLight` receives 
information from a sensor whether the door is opened or closed via the
`doorSignal` port of type `Boolean`. Furthermore, it receives information via
the `lightSignal` port, whether the contact of the light switch is currently 
closed. These signals are sent to an `ORGate` which evaluates to `true` if 
either of the incoming signals forwarded by the `InteriorLight`is `true`.
Via the `c` port of the `ORGate` this evaluation is then sent to the `signal`
port of the `cntr` component of type `LightController`. This component turns 
on the light, and on state switches informs about the light status switch via
its `status` port, which forwards to the `status` port of the `InteriorLight`.


## Symboltable

The MontiArc language family defines the following symbols:

- **ComponentTypeSymbol** The symbol of a component type providing the
component's specification. Spans a scope that holds the symbols representing
the type parameters, component fields, configuration parameters, ports, and
subcomponent instances of the component type.
- **ComponentInstanceSymbol** The symbol of a component instance. A component
instance has a type corresponding to a compnent type symbol.
- **PortSymbol** The symbol of a port. A port has a type corresponding to a
type symbol.

Additionally, the MontiArc language family imports the following symbols:

- **FieldSymbol** The symbol of a component field or configuration parameter.
- **TypeVarSymbol** The symbol of a type parameter of a component. 
- **TypeSymbol** The symbol of a data type of ports, parameters, and component 
fields.

<img src="pics/MontiArc.SymbolTable.PNG" alt="drawing" height="400px"/>

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