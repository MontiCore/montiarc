---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Variability

!!! warning
    Variability support is rudimentary. Expect significant performance impacts during compilation.
    Not all features are supported by the simulator.

## Features

Features have a name and are of type Boolean. They act similar to parameters as they are assigned by during and instantiation.

```montiarc
feature NAME1, NAME2;
```

## Variation Points

Variation points are the core building block of variability. They allow for architecture variability.
Variation points have a condition that needs to be satisfied for the included elements to be added to the base component.

```montiarc
varif (BOOLEAN_EXPRESSION) {
  // MontiArc elements
}
```

Inside of variation points we can include all types of MontiArc elements (e.g., ports, connectors, subcomponents, automata, other variation points, etc.).
Only features and constraints need to be defined outside of these.

The Boolean expression has to be evaluated statically; this means it cannot contain references to [ports](./Interfaces.md) and [fields](../Behavior/Fields.md).

## Constraints

Constraints can restrict the possible variants of a component by defining Boolean expressions that have to be satisfied for a component to be considered valid.

```
constraint(BOOLEAN_EXPRESSION);
```

### Binding Features

Constraints are also used to bind features to specific values. 

Given a subcomponent named `sub` with a feature `f`, we can constrain the feature to a specific value:
```
constraint(sub.f);
```

This binding respects subcomponents and other constraints defined in the component and is solved automatically. 
