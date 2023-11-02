<!-- (c) https://github.com/MontiCore/monticore -->

The behavior of a component is formally defined as the function that determines the output value of a component, based on current and historic input values of the component. 
There are different ways to describe the behavior of components:

* At the heart are expression that allow calculating new output values on the basis of input values.
* automatons that define state-based behavior with finite state machines
* imperative code blocks that facilitate algorithm-based behavior programming
* Additionally, variables may be used to persist state as time progresses

A second aspect of behavior definitions is the timing of when components' input values are evaluated and when output values are calculated based on them.
This is especially important when multiple components are composed to a new one:
Do they always calculate the next output values at the same time, or independent of each other?
MontiArc developers will define which timing paradigm each component implements to answer this question.
