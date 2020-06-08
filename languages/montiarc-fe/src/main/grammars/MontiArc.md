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
component LightController {
  port in Boolean switchStatus, alarmStatus,
       in DoorAngle doorStatus,
       out OnOff onOffCmd;
  
  Arbiter arb [cmd -> onOffCmd;]
  AlarmCheck ac(5) [warn -> arb.warn;]
  DoorEval eval; 
  eval.offReq -> arb.offReq;
  switchStatus -> arb.switchStatus;
  switchStatus -> eval.switchStatus;
  doorStatus -> eval.doorStatus;
  alarmStatus -> ac.alarm;
}

```
Example component of a car interior light controller `LightController` 
that turns on if a user manually presses a light switch or the car door opens.
The `LightController` receives information from a sensor how far the door is 
opened or if it is closed via the `doorStatus` port of type `DoorAngle`. 
Furthermore, it receives information via the `switchStatus` port, whether 
the contact of the light switch is currently closed. These signals are sent 
to the component `eval` of type `DoorEval` which evaluates whether the light
should be turned on or off based on the current switch and door status. 
Based on this evaluation, the `eval` component may send an off requeset via
its `offReq` port to the component `arb` of type `Arbiter`. The arbiter also
receives warings if an alarm is triggered. Based on this, the arbiter 
evaluates whether the light should be turned on or off and sends a
corresponding command via its `cmd` port, which is then forwarded by
the `LightController` via its `onOffCmd` port.

## Symboltable
- De-/Serialization functionality for the symbol table 
  ([`XXX serialization`][XXX where to be found?])
- [`XXX CD4AnalysisSymbolTableCreator`][CD4ASTC]
  handles the creation and linking of the symbols

## Symbol kinds used by the MontiArc language (importable):

TODO @DS: check the following sections

The MontiArc language imports the following symbols kinds:
- `FieldSymbol` The symbol of a component field or configuration parameter.
                        Kind is imported, symbols are defined locally only.
- `TypeVarSymbol` The symbol of a type parameter of a component. 
                        Kind is imported, symbols are defined locally only.
- `TypeSymbol` The symbol of a data type of ports, parameters, and 
                        components fields.
                        Kind and symbol instances are imported.

## Symbol kinds defined by the MontiArc language (exported):
The MontiArc language family defines the following symbols:

- **`ComponentTypeSymbol`** The symbol of a component type providing the
  component's specification. Spans a scope that holds the symbols representing
  the type parameters, configuration parameters, component fields, ports, and
  subcomponent instances of the component type.
- **`ComponentInstanceSymbol`** The symbol of a component instance. A component
  instance has a component type.
- **`PortSymbol`** The symbol of a port. A port has a type corresponding to a
  (normal) type expression, like `int`, `Signal`, or also `Set<String>`.

Please note that MontArc keeps the type expressions knwon from MontiCore's types
and the *component types* already syntactically disjoint. They cannot be mixed up.

## Symbols imported by MontiArc models:
- **`TypeSymbol`**: Through explicit import statements, a list of artifacts
                may provide types.
  - Subsequent extensions of MontiArc will also use the signatures provided
    by these types (e.g. method signatures).
- **`ComponentTypeSymbol`**: MontiArc models may import and use component types
  defined in other models. A `ComponentTypeSymbol` additionally knows
  symbols exported by the component, such as `PortSymbol`s and 
  symbols for their parameters.
- no other kind of symbols is imported. E.g., isolated fields or methods 
  are not imported.

## Symbols exported by MontiArc models:
- **`ComponentTypeSymbol`** each MontiArc model provides at least one newly defined
  and externally usable component type.
  If further (inner) component types are defined, then all these component types are
  exported as well (MontiArc model is usable as library). 
- Inner component types need to be qualified if used outside the defining artifact. 
- **`PortSymbol`** s are exported as elements of the component types and
  only accessible through their components.
- **`FieldSymbol`**s and **`TypeVarSymbols`** are used as parameters 
  resp. type parameters of their component types as well.
- Fields and inner component instances are not available outside a component.
  Components encapsulate these elements and only make them accessible for 
  communication through their port interfaces.
- The exported symbols are available in the symbol table  `*.masym`.


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


