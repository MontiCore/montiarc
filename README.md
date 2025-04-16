<!-- (c) https://github.com/MontiCore/monticore -->
<h1 align="center">
  <picture>
    <img width="400" src="./docs/img/Logo.png" alt="MontiArc">
  </picture>
</h1>

<h4 align="center">
  <a href="https://monticore.github.io/montiarc">Documentation</a> |
  <a href="https://monticore.github.io/montiarc/v7.8/GettingStarted">Getting Started</a> |
  <a href="https://www.monticore.de/">MontiCore</a>
</h4>

# The MontiArc Architecture Description Language

MontiArc is an architectural definition language for component and connector models with enhanced connection facilities,
hierarchical decomposition, behavior description, and variability.

In MontiArc, architectures are described as component and connector systems in which autonomously acting components 
perform computations. Communication between components is regulated by connectors between the components’ interfaces, 
which are stable and built up by typed, directed ports. Components are either atomic or composed of connected 
subcomponents. Atomic components yield behavior descriptions in the form of embedded time-synchronous port automata 
or via integration of handcrafted code. For composed components, the behavior emerges from the behavior of their 
subcomponents. 

© https://github.com/MontiCore/monticore

### Further Information

* [MontiArc documentation](https://monticore.github.io/montiarc)
  * [Setup](https://monticore.github.io/montiarc/v7.8/GettingStarted/Setup)
  * [Contributing](https://monticore.github.io/montiarc/v7.8/Contributing)
  * [FAQ](https://monticore.github.io/montiarc/v7.8/FAQ)
* [MontiCore documentation](https://www.monticore.de/)
* [Publications about MBSE, MontiArc, and MontiCore](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/HEAD/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

## About MontiArc
Language features of the ADL MontiArc include hierarchical decomposition of components, 
subtyping by structural inheritance, component type definitions and reference declarations for reuse,
generic component types and configurable components, syntactic sugar for connectors,
static and dynamic variability,
and controlled implicit creation of connections and subcomponent declarations.

### Model Cyber-Physical Systems
Cyber-Physical Systems are inherently distributed, interacting in various ways using signals, messages and data. However, model-based development of Cyber-Physical Systems becomes particularly interesting, when modeling the context of the software control, i.e. electric and hydraulic signals as well as physical material 
(streams of fluids or gadgets) to simulate the system under development early.

MontiArc is suited for modeling all kinds of cyber-physical systems.

### Built-in Simulation Support
MontiArc offers built-in simulation support. Based on its mathematical foundation of Focus,
MontiArc is constructive enough to be used in a simulator or for code generation.
