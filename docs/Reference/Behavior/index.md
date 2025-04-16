---
icon: material/play
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

The behavior of a component is formally defined as the function that determines the output value of a component, based on current and historic input values of the component. 
There are different ways to describe the behavior of components:

* At the heart are expressions that allow calculating new output values based on input values.
* Automatons that define state-based behavior with finite state machines
* Imperative code blocks that facilitate algorithm-based behavior programming
* Additionally, variables may be used to persist state as time progresses


<div class="grid cards" markdown>

-   :material-state-machine: &nbsp;
    __Automata__

    ---

    Finite state machines
    
    ---

    [:octicons-arrow-right-24: Read more](./Automata.md)

-   :octicons-code-16: &nbsp;
    __Compute__

    ---

    Imperative code blocks

    ---

    [:octicons-arrow-right-24: Read more](./Compute.md)

-   :material-soccer-field: &nbsp;
    __Fields__

    ---

    Store state information in component fields

    ---

    [:octicons-arrow-right-24: Read more](./Fields.md)

-   :material-language-java: &nbsp;
    __Handwritten Code__

    ---

    Write native platform code

    ---

    [:octicons-arrow-right-24: Read more](./HWC.md)

</div>
