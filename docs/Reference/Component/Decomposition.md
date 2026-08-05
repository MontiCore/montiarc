<!-- (c) https://github.com/MontiCore/monticore -->
# (De-) Composition

Components can be decomposed to structure a system, separate concerns, and 
reduce complexity by distributing system functionality across multiple smaller 
components. 

In MontiArc, component types define the decomposition of components in terms 
of subcomponents and connectors between the components' interfaces. 

## Subcomponent Declaration

Subcomponents are declared in the body of a component type, specifying the 
subcomponents' types and stating their names. A simple subcomponent declaration 
looks like 

```montiarc
TYPE SUB;
```

where 

* `TYPE` is the subcomponent's (qualified) type (reference) 

* `SUB`  is the subcomponent's unique name (defining)

Subcomponents are directly instantiated alongside their declaration. 

A component can have multiple subcomponents of different types but also 
multiple subcomponents of the same type. For convenience, multiple subcomponents
of the same type can be instantiated by stating their names in a comma-separated 
list after the component's type, which looks like 

```montiarc
TYPE SUB1, SUB2;
```

and is a shorthand notation for 

```montiarc
TYPE SUB1;
TYPE SUB2;
```

## Arguments

The instantiation of a component may require arguments, which configure the 
component's initial state or behavior.
The component's type declaration defines the number, order, and type of 
arguments required. See [parameters](./Parameter.md) for more information.

Arguments must be provided during component instantiation and are listed after 
the subcomponents' name in round brackets (`( )`).
A subcomponent declaration with arguments looks like

```montiarc
TYPE SUB(ARGS);
```

where `ARGS` is a comma-separated list of arguments of the form 
`ARG1, ARG2, ...., ARGn` where `ARG1`, `ARG2`, and so forth until `ARGn` are 
the first, second, and so forth until the n-th argument. All arguments are defined using [expressions](../Concepts/Expressions/index.md).

## Type Arguments

Generic components require type arguments that replace the generic's type 
parameters with the actual types.
The component's type declaration defines the number of type arguments needed, 
their order, and potential typing restrictions. 
See [Generics](./Generics.md) for more information.
The signature of a generic component depends in part on the provided type 
arguments; that is, type arguments may define the actual typing of ports and 
parameters.

Type arguments must be provided with the subcomponent declaration alongside 
the component type in angle brackets (`< >`).

```montiarc
TYPE<TARGS> SUB; 
```


where `TARGS` is a comma-separated list of arguments of the form 
`TARG1, TARG2, ...., TARGn` where `TARG1`, `TARG2`, and so forth until `TARGn` 
are the first, second, and so forth until the n-th type argument.

For example, 

```montiarc
Delay<int> delay;
```

declares and instantiates a subcomponent `delay` of type `Delay` with type 
argument `int`.

!!! warning
    Note that only data types may be used as type arguments. 
    Component types **cannot** be used as type arguments.
