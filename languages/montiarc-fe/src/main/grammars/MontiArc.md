<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

# MontiArc - Language for Modelling Architecture

[[_TOC_]]

MontiArc is an architecture modeling language and framework that provides platform 
independent modeling capabilities. MontiArc is **extensible** in several dimensions:
1. **Behavioral modelling** languages, such as statecharts or logic formulas, can be easily embedded
2. The **type system** is configurable, e.g., types defined using class diagrams
can be imported or *SI-unit types* can be provided, 
3. MontiArc models can be combined with other structural or behavioral models by exchange of symbols (e.g. through import statements). This connects independently developed models in both directions.  
4. The **code generation** framework can be adapted and extended for 
simulation as well as for various technological and language target platforms.

The MontiArc language covers **components** their **ports**, **connectors** between
components and is shipped with embedded statecharts for component behavior description 
as a standard extension.

The MontiArc core repository contains the common basis
of the MontiArc architecture description language. This project is maintained
by the [Working Group for Model-Driven Systems Engineering (MDSE)][mdse].

The language for MontiArc Architecture diagrams is composed from five grammars,
which also might be used independently:
- **ArcBasis**: basic language component for architectures consisting
of ports, components, and connectors
- **ComfortableArc**: extension of the architecture basis with comfort elements
to ease the description of architectures
- **GenericArc**: extension of the architecture basis for generic component types
- **ArcCore**: builds the core of architectural modelling by aggregating the above 
  mentioned languages components (still without concrete expressions, literals, etc.)
- **VariableArc**: extension of the architecture basis for variable component types,
adding variable elements and constraints for their inclusion
- **MontiArc**: extends ArcCore to a complete language with defined expressions,
  literals, and types. MontiArc furthermore embedds statecharts for behavior description.

The main grammar file is [`MontiArc`][MontiArcGrammar].

# Note to the relation between MontiArc and **SysML**

SyML currently provides Block 
Definition Diagrams (BDD) and Internal Block Diagrams (IBD) covering very similar concepts.
One main difference is, that in SysML the definition of components (there called blocks) 
is separated from the use, while MontiArc allows an integrated definition.
I.e. the same model covers definition of the component and its ports and 
the internal structuring and wiring into subcomponents.

Of course SysML provides many more language constructs that the MontiArc 
language family does not directly provide. The MontiArc approach instead is to 
come up with a modular and extensible language family that allows individual companies, 
or even projects to define their own version of architectural modelling
specific to the domain and thus typically effective to use.

This approach comes from the experience that while many projects use SysML, they typically 
have to bend and tweak it into a "DSL based on SysML" in various forms.
MontiArc allows that in an additive, modular form and is e.g. fully compatible
with the many variants of expression, statement and typing language components that
[`MontiCore`][MontiCore] provides.

# The MontiArc Architecture Description Language

In MontiArc, architectures are topologies of component and connectors in which 
logically autonomous components perform computations. Connectors define the
interaction between the components' interfaces, which consist of typed, directed
ports. Components are either atomic or composed of connected subcomponents.
Atomic components yield behavior descriptions in the form of embedded 
time-synchronous port automata, or via integration of handcrafted code. For 
composed components, the behavior emerges from the behavior of their subcomponents.

MontiArc is desined to define logical or physical components and can be mapped to 
various target platforms, depending on the according interpretations, whether 
a component will be a pyhsical unit (i.e. CPU, a virtual CPU unit, a process, 
a thread, a logical function that is appropriately scheduled or even a networked IOT' 
gadget).

## Example for MontiArc (from the Electronics Domain)

```
package ma.sim;

component RSFlipFlop {
  behavior timesynchronous;   // relatively simple timing model
  port                        // ports = signature of the component
    in Boolean set,
    in Boolean reset,
    out Boolean q,
    out Boolean notQ;

  SyncNOR nor1, nor2;         // two subcomponents

  // definition of anothee subcomponent modelling delay
  // component is parameterized in two ways:
  //   T: type of ports and
  //   delay: the delay it introduces
  component FixDelay<T>(int delay) {
    int counter;
    port
      in T portIn,
      out T portOut;
  }
  
  FixDelay<Boolean>(1) d1, d2; // two delays added

  // connection structure   
  set -> nor1.in1;
  reset -> nor2.in1;
  
  nor1 -> notQ, d1.portIn;
  nor2 -> q, d2.portIn;

  d1.portOut -> nor2.in2;
  d2.portOut -> nor1.in2;
}
```
An example of a reset-set flip-flop
- Definition of two component types `RSFlipFlop` and `FixDelay`
- `FixDelay` is an inner component type of `RSFlipFlop`
- Components include attributes, ports and subcomponents, the 
  latter beeing instances of component types
- Ports have a type (like `Boolean`) and a direction (`in` or `out`)
- Component types can define parameters and generic type parameters 
  as configuration options for component instantiation
- Connectors are defined between the interfaces of components, 
  directing the flow of messages between them
- In practice the connection structure can often be inferred from the port 
  names and types. Keyword `autoconnect` allows to utilize this.

Further examples in other domains can be found [here][Applications].

## Available handwritten Extensions

### AST
- [`ASTComponentInstantiation`][ASTComponentInstantiation]
  adds methods to retrive the name of component instances
- [`ASTComponentType`][ASTComponentType]
  adds methodes for easy access of the component's ports, 
  fields, subcomponents and inner component types.
- [`ASTConnector`][ASTConnector] 
  adds methods to retrive the name of source and targets
- [`ASTPortAccess`][ASTPortAccess]
  adds a method to retrive the qualified name of a port
- [`ASTPortDeclaration`][ASTPortDeclaration] 
  adds methods to determine whether a port is incoming 
  or outgoing

### Parser for MontiArc
- [`MontiArcParser`][MontiArcParser]
  is extended to check the conformity of file name and
  topmost component type during parsing and to perform 
  additional transformations after parsing

## Symbol kinds used by the MontiArc language (importable):

The MontiArc language imports the following symbols kinds:
- `TypeSymbol` defines a data type and can be used for ports, parameters, and 
  component fields. Kind and symbol instances are imported. 
- `FieldSymbol` can be imported as component field or configuration parameter
  of an imported component.
  Kind is imported, component fields are defined locally only, while 
  configuration parameters are imported as part of component types.
- `TypeVarSymbol` The symbol of a type parameter of a component. 
  Kind is imported, symbols are defined locally and imported as
  part of component types.

## Symbol kinds defined by the MontiArc language (exported):
The MontiArc language family defines the following symbols:

- `ComponentTypeSymbol` the symbol for component types providing the
  component's definition. Holds symbols of the component's parameters,
  type parameters, ports, fields, and subcomponents.
- `ComponentInstanceSymbol` the symbol for component instances. A component
  instance has a component type and thus can be used to connect it's ports.
- `PortSymbol` the symbol of a port. A port has a type for the 
  data flowing on it, which thus corresponds to a
  (normal) type expression, like `int`, `Signal`, or also `Set<String>`.

Please note that MontiArc keeps the type expressions knwon from MontiCore's types
and the *component types* disjoint. The intention is that they cannot be mixed up.
Components cannot be misused as classes and vice versa -- this is a lecture
that was learn't form the type confusion occuring in SysML, where ultimatively 
everything is understood as class.

## Symbols imported by MontiArc models:

- `TypeSymbol`: Through explicit import statements types from other artifacts 
  can be used for loacal variable, parameters and port types.
  - The signatures provided by these types (e.g., method signatures) can be used
  in expressions.
- `ComponentTypeSymbol`: MontiArc models may import and use component types
  defined in other component models. A `ComponentTypeSymbol` knows
  symbols exported by the component, such as `PortSymbol`s and symbols 
  for their parameters.
- No other kind of symbols is imported, e.g., no isolated fields or methods 
  are imported.

## Symbols exported by MontiArc models:

- `ComponentTypeSymbol`: Each MontiArc model provides at least one newly defined
  and externally usable component type.
  If further (inner) component types are defined, then all these component types are
  exported as well (MontiArc models are usable as libraries). 
- Inner component types need to be qualified if used outside the defining artifact. 
- `PortSymbol`: These are exported as elements of the component types and
  only accessible through their components.
- `FieldSymbol` and `TypeVarSymbols`: These are used as parameters 
  resp. type parameters of their component types and expored as elements 
  of the component types as well.
- Fields and inner component instances are not available outside a component.
  Components encapsulate these elements and only make them accessible for 
  communication through their port interfaces.
- The exported symbols are available in the symbol table `*.masym`.

## Functionality

### Context Conditions
- [`MontiArcCoCos`][MontiArcCoCos] combines all CoCos for all its sublanguages.
- The individual CoCos can be found in the corresponding directory of each language.
- The context conditions ensure the semantic correctness, here is a list of some of the important ones:
  - Uniqueness of names of, e.g., subcomponents, ports, and fields
  - Component inheritance is free of cycles
  - Usage of instances matches type, e.g., used ports exist
  - Type compatibility of connected ports

### PrettyPrinter
- ['MontiArcFullPrettyPrinter`][MontiArcFullPrettyPrinter] contains the basic pretty printer for MontiArc

[se-rwth]: http://www.se-rwth.de
[mdse]:http://www.se-rwth.de/teams/mdse/
[MontiCore]:http://www.monticore.de/

[Applications]: ./applications

[ASTComponentInstantiation]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTComponentInstantiation.java
[ASTComponentType]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTComponentType.java
[ASTConnector]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTConnector.java
[ASTPortAccess]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTPortAccess.java
[ASTPortDeclaration]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTPortDeclaration.java

[MontiArcCoCos]: ./languages/montiarc-fe/src/main/java/montiarc/_cocos/MontiArcCoCos.java
[MontiArcParser]: ./languages/montiarc-fe/src/main/java/montiarc/_parser/MontiArcParser.java
[MontiArcFullPrettyPrinter]: ./languages/montiarc-fe/src/main/java/montiarc/_visitor/MontiArcFullPrettyPrinter.java

[MontiArcGrammar]: ./languages/montiarc-fe/src/main/grammars/MontiArc.mc4

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
