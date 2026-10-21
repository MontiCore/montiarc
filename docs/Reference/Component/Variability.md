---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Variability

A component with features and variation points describes a family of components.
MontiArc checks such a component family as a whole: instead of checking each 
variant individually, MontiArc reason about the conditions under which the 
elements of a component are present.

!!! warning
    Code generation does not support variability. Components with features or 
    variation points can be statically analyzed, but not simulated. The analysis 
    of variable components is therefore disabled when generating code.

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
} else {
  // MontiArc elements
}
```

Inside of variation points we can include all types of MontiArc elements (e.g., ports, connectors, subcomponents, automata, other variation points, etc.).
Elements of the same name may be declared in variation points that are never present together, e.g., a port `i` of
type `int` in the `then` branch and a port `i` of type `boolean` in the `else` branch.
Only features need to be defined outside of variation points.

The Boolean expression has to be evaluated statically; this means it cannot contain references to [ports](./Interfaces.md) and [fields](../Behavior/Fields.md).
It may refer to the features and parameters of the component and to the features of its subcomponents (e.g., `sub.f`).

## Constraints

Constraints restrict the valid configurations of a component by defining Boolean expressions that have to be
satisfied for a configuration to be valid. A constraint inside a variation point only applies to the configurations in
which the variation point is present.

```
constraint(BOOLEAN_EXPRESSION);
```

### Binding Features

Constraints are also used to bind the features of subcomponents.
Given a subcomponent named `sub` with a feature `f`, we can constrain the feature to a specific value:
```
constraint(sub.f);
```

The constraints of a component have to determine the features of its subcomponents, and they have to imply the
constraints of its subcomponents: if `sub` has the constraint `constraint(f);`, the component instantiating it has to
restate `constraint(sub.f);`. Constraints over the parameters of a subcomponent are implied by its arguments or the
default values of its parameters.

## Analysis

MontiArc checks all valid configurations of a family of components at once 
and reports an error if a context-condition is violated in some valid 
configuration. For example, an error is reported if a connector connects two 
ports of incompatible types in some configuration, even if there are other 
configurations in which the same connector connects ports of compatible types.

However, the values of some constraint expressions cannot be determined 
statically, e.g., the results of method calls, comparisons of strings or other
values of types without a logical representation. The analysis considers every 
possible value of these. A context condition that is violated for
some, but not all, of these values reports a warning instead of an error.

Besides the usual context conditions, the analysis reports the following findings:

| Finding                                                                    | Severity |
|----------------------------------------------------------------------------|----------|
| The constraints of a component cannot be satisfied (`0xC1401`)             | Error    |
| The constraints do not determine the features of subcomponents (`0xC1404`) | Error    |
| The constraints do not imply the constraints of a subcomponent (`0xC1406`) | Error    |
| A feature is not selected in any valid configuration (`0xC1407`)           | Warning  |
| A variation point is not present in any valid configuration (`0xC1408`)    | Warning  |

The analysis requires an SMT solver and may slow down the compilation of large component families.

## Analysis and code generation

The analysis of variable components and code generation exclude each other:

* The MontiArc CLI analyzes variable components unless the option `-novar` is set. Generating code (option `-o`)
  implies `-novar`.
* The Gradle plugin analyzes variable components if the option `checkVariability` is set to `true`. Generating code
  (option `generateCode`) implies `checkVariability` set to `false`.

Without the analysis of variable components, features, variation points, and constraints are rejected (`0xC1198`),
as they cannot be checked without it.
