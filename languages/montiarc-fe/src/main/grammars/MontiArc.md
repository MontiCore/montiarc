<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

# MontiArc - Language for Modelling Architecture

MontiArc is an architecture modeling language and framework that provides platform 
independent modeling capabilities. MontiArc is **extensible** in several dimensions:
1. **Behavioral modelling** languages, such as statecharts, can be easily embedded
2. External **types**, e.g., defined using class diagrams, can be imported
3. The **code generation** framework can be adapted and extended for various target platforms.

The MontiArc language covers **components** their **ports**, **connectors** between
components and is shipped with embedded statecharts for component behavior description 
as a standard extension.

The MontiArc Core repository contains the common basis
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
- **MontiArc**: extends ArcCore to a complete language with defined expressions,
  literals, and types. MontiArc furthermore embedds statecharts for behavior description.

The main grammar file is [`MontiArc`][MontiArcGrammar].

# The MontiArc Architecture Description Language

In MontiArc, architectures are topologies of component and connectors in which 
autonomously acting components perform computations. Connectors define the
interaction between the components' interfaces, which consist of typed, directed
ports. Components are either atomic or composed of connected subcomponents.
Atomic components yield behavior descriptions in the form of embedded 
time-synchronous port automata, or via integration of handcrafted code. For 
composed components, the behavior emerges from the behavior of their subcomponents.

## Example for MontiArc

```
package ma.sim;

component RSFlipFlop {
  behavior timesynchronous;
  port
    in Boolean set,
    in Boolean reset,
    out Boolean q,
    out Boolean notQ;

  SyncNOR nor1, nor2;

  component FixDelay<T>(int delay) {
    int counter;
    port
      in T portIn,
      out T portOut;
  }
  
  FixDelay<Boolean>(1) d1, d2;
  
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

Further examples can be found [here][Applications]

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
- `TypeSymbol` The symbol of a data type of ports, parameters, and 
  component fields. Kind and symbol instances are imported.
- `FieldSymbol` The symbol of a component field or configuration parameter.
  Kind is imported, symbols are defined locally only.
- `TypeVarSymbol` The symbol of a type parameter of a component. 
  Kind is imported, symbols are defined locally only.

## Symbol kinds defined by the MontiArc language (exported):
The MontiArc language family defines the following symbols:

- `ComponentTypeSymbol` The symbol of a component type providing the
  component's definition. Holds symbols of the component's parameters,
  type parameters, ports, fields, and subcomponents.
- `ComponentInstanceSymbol` The symbol of a component instance. A component
  instance has a type corresponding to a component type.
- `PortSymbol` The symbol of a port. A port has a type corresponding to a
  (normal) type expression, like `int`, `Signal`, or also `Set<String>`.

Please note that MontArc keeps the type expressions knwon from MontiCore's types
and the *component types* disjoint. They cannot be mixed up.

## Symbols imported by MontiArc models:
- `TypeSymbol`: Through explicit import statements artifacts may provide types.
  - The signatures provided by these types (e.g., method signatures) can be used
  in expressions of the corresponding type system.
- `ComponentTypeSymbol`: MontiArc models may import and use component types
  defined in other component models. A `ComponentTypeSymbol` additionally knows
  symbols exported by the component, such as `PortSymbol`s and symbols 
  for their parameters.
- No other kind of symbols is imported, e.g., isolated fields or methods 
  are not imported.

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

### CoCos
- [`MontiArcCoCos`][MontiArcCoCos] combines all CoCos for all its sublanguages.
- The individual CoCos can be found in the corresponding directory of each language.
- The context conditions ensure the semantic correctness, here is a list of some of the important ones:
  - Uniqueness of names of, e.g., subcomponents, ports, and fields
  - Component inheritance is free of cycles
  - Usage of instances matches type, e.g., used ports exist
  - Type compatibility of connected ports

### PrettyPrinter
- ['MontiArcPrettyPrinterDelegator`][MontiArcPrettyPrinterDelegator] contains the basic pretty printer for MontiArc

[se-rwth]: http://www.se-rwth.de
[mdse]:http://www.se-rwth.de/teams/mdse/

[Applications]: ./applications

[ASTComponentInstantiation]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTComponentInstantiation.java
[ASTComponentType]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTComponentType.java
[ASTConnector]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTConnector.java
[ASTPortAccess]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTPortAccess.java
[ASTPortDeclaration]: ./languages/arc-fe/src/main/java/arcbasis/_ast/ASTPortDeclaration.java

[MontiArcCoCos]: ./languages/montiarc-fe/src/main/java/montiarc/cocos/MontiArcCoCos.java
[MontiArcParser]: ./languages/montiarc-fe/src/main/java/montiarc/_parser/MontiArcParser.java
[MontiArcPrettyPrinterDelegator]: ./languages/montiarc-fe/src/main/java/montiarc/_visitor/MontiArcPrettyPrinterDelegator.java

[MontiArcGrammar]: ./languages/montiarc-fe/src/main/grammars/MontiArc.mc4
