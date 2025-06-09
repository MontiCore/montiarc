---
hide:
  - navigation
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

<h1 align="center">
  <picture>
    <img width="400" src="img/Logo.png" alt="MontiArc">
  </picture>
</h1>

# The MontiArc Architecture Description Language

MontiArc is a textual architecture description language (ADL) for the
specification and modeling of cyber-physical systems.
The architecture of a cyber-physical system is described as a [component](./Reference/Component/index.md) and 
[connector](./Reference/Component/Connectors.md) (C&C) system, in which autonomously acting components perform computations.
Components have clear defined [interfaces](./Reference/Component/Interfaces.md) that consists of stable, typed, directed ports.

Each component defines a cyber-physical function, which maps streams of input
messages to stream of output messages.
This function is the behavior of the corresponding cyber-physical systems.
Behavior can be specified through atomic [behavior descriptions](./Reference/Behavior/index.md), 
such as [statecharts](./Reference/Behavior/Automata.md), or through the 
[composition](./Reference/Component/Decomposition.md) of subcomponents to form larger systems.

The MontiArc infrastructure provides essential functionality for verifying the
validity of component and connector models.
Components can be translated into Java simulations to simulate, analyze,
and validate their behavior, for example through automated tests.
In addition to the basic modeling elements \- components, ports, and connectors \-
MontiArc supports advanced modeling concepts such as component 
[parameterization](./Reference/Component/Parameter.md),
[variability](./Reference/Component/Variability.md), 
dynamic reconfiguration, and [generics](./Reference/Component/Generics.md).

<div class="grid cards" markdown>

-   :material-rocket-launch: &nbsp;
    __Getting Started__

    ---

    Is this your first time using MontiArc? Set up a project and start modeling.
    
    ---

    [:octicons-arrow-right-24: Take the Tour](./GettingStarted/index.md)<br/>
    [:octicons-arrow-right-24: Installation & Setup](./GettingStarted/Setup.md)

-   :material-tools: &nbsp;
    __Usage__

    ---

    Learn how to use the tooling.<br/><br/>

    ---

    [:octicons-arrow-right-24: Read more](./Usage/index.md)

-   :material-folder: &nbsp;
    __Reference__

    ---

    A detailed description of how MontiArc models are defined and work.

    ---

    [:octicons-arrow-right-24: Read more](./Reference/index.md)

-   :material-book-open-variant-outline: &nbsp;
    __Library__

    ---

    A collection of standard components and functions included in every model.

    ---

    [:octicons-arrow-right-24: Read more](./Library/index.md)

-   :material-chat-question: &nbsp;
    __FAQ__

    ---

    Get answers to frequently asked questions.<br/><br/>

    ---

    [:octicons-arrow-right-24: Read more](./FAQ/index.md)

-   :material-bug: &nbsp;
    __Contributing__

    ---

    Have you found an issue in MontiArc or the documentation?

    ---

    [:octicons-arrow-right-24: Find out how to report issues](./Contributing/index.md)

-   :material-license: &nbsp;
    __License__

    ---

    Learn about the license and how you can use the generated code.

    ---

    [:octicons-arrow-right-24: Read more](https://monticore.github.io/monticore/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL/)

</div>


---

## Further Information

* [Setup](https://monticore.github.io/montiarc/v7.8/GettingStarted/Setup)
* [Contributing](https://monticore.github.io/montiarc/v7.8/Contributing)
* [FAQ](https://monticore.github.io/montiarc/v7.8/FAQ)
* [Publications](https://www.se-rwth.de/publications/)
* [License](https://github.com/MontiCore/monticore/blob/HEAD/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
