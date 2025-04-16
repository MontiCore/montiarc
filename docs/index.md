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

MontiArc is an architectural definition language for component and connector models with enhanced connection facilities,
hierarchical decomposition, behavior description, and variability.

Architectures are described as [component](./Reference/Component/index.md) and [connector](./Reference/Component/Connectors.md) systems in which autonomously acting components 
perform computations. Communication between components is regulated by connectors between the components’ [interfaces](./Reference/Component/Interfaces.md), which are stable and built up by typed, directed ports. Components are either atomic or composed
of connected subcomponents. Atomic components yield [behavior descriptions](./Reference/Behavior/index.md). For [composed components](./Reference/Component/Decomposition.md), the behavior emerges from the behavior of their subcomponents. 


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

* [MontiCore documentation](https://www.monticore.de/)
* [Publications about MBSE, MontiCore, and MontiArc](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/HEAD/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
