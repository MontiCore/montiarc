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

Subcomponents are directly instantiated alongside there declaration. 

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
component's initial state.
The component's type declaration defines the number, order, and type of 
arguments required.

Arguments must be provided during component instantiation and are listed after 
the subcomponents' name in round brackets (`( )`).
A subcomponent declaration with arguments looks like

```montiarc
TYPE SUB(ARGS);
```

where `ARGS` is a comma-separated list of arguments of the form 
`ARG1, ARG2, ...., ARGn` where `ARG1`, `ARG2`, and so forth until `ARGn` are 
the first, second, and so forth until n-th argument. All arguments are defined 
using expressions.

## Type Arguments

Generic components require type arguments that replace the generic's type 
parameters with the actual types.
The component's type declaration defines the number of type arguments needed, 
their order, and potential typing restrictions.
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
are the first, second, and so forth until n-th type argument.

For example, 

```montiarc
Delay<Integer> delay;
```

declares and instantiates a subcomponent `delay` of type `Delay` with type 
argument `Integer`.

Note that only data types may be used as type arguments. 
Component types **cannot** be used as type arguments.

## Connectors

Connectors connect the interfaces (ports) of components, defining the flow of 
messages and determining which components communicate whit each other. 
Connectors are defined in the body of the component type. A connector looks 
like 

`SOURCE` -> `TARGET`;

where 

* `SOURCE` is the (qualified) name of the source port (reference)

* `TARGET` is the (qualified) name of the target port (reference)


The source of a connector can be

* an incoming port of the component

* an outgoing port of a subcomponent

The target of a connector can be

* an outgoing port of the component

* an incoming port of a subcomponent

A port can only be targeted by a single connector but can be the source of 
multiple connectors. For convenience, a connector can define multiple targets. 
The targets are given as a comma-separated list, which looks like

```montiarc
SOURCE -> TARGET1, TARGET2;
```

which is a shorthand notation for 

```montiarc
SOURCE -> TARGET1; 
SOURCE -> TARGET2; 
```

We distinguish three kinds of connectors:

* A connector from an incoming port of the component to an incoming port of a 
subcomponent, forwarding messages received by the component to one of its
subcomponents. E.g., `i -> sub.j; ` forwards message received by port `i` to 
port `j` of subcomponent `sub`.

* A connector from an outgoing port of a subcomponent to an outgoing port of 
the component, forwarding messages send by the subcomponent. E.g., `sub.o -> p;` 
forwards messages send by subcomponent `sub` on port `o` via port `p`.

* A connector from an outgoing port of a subcomponent to an incoming port of 
a subcomponent (potentially the same subcomponent), sometime called a hidden 
connector. E.g., `sub1.o -> sub2.i;` connects port `o` of subcomponent `sub1` 
to port `i` of subcomponent `sub2`.

## Feedback

If subcomponents form a communication circle along the direction of connectors, 
then a subcomponent may communicate with itself, either directly or indirectly 
across other subcomponents. We call this a feedback loop. 

In a direct feedback loop the output of a component is directly connected to 
the input of the component. The component communicates directly with itself.

```montiarc
sub.o -> sub.i; 
```

In an indirect feedback loop the output of a component is connected to the 
input of the component indirectly across one to multiple subcomponents. The 
component communicates indirectly with itself.

```montiarc
sub1.o -> sub2.i;
sub2.o -> sub3.i;
sub3.o -> sub1.i;
```

Communication is abstracted to be instantaneous. However, for the propagation 
of timing events in feedback loops we need delay. Otherwise, the components output at some point in time would depend on itself. 

Where the delay happens in the communication circle is irrelevant, just there
needs to be some kind of delay. 

Delay can be introduced through the stereotype `<<delayed>>` on the output 
port of an atomic component, specifying outputs on that port are delayed by 
one Tick. For simplicity, we can also introduce a specific delay.

Delay for (timed) event automata: 

```montiarc
component Delay<T> {
  port in T i;
  port <<delayed>> out T o;
  
  <<timed>> automaton {
    initial state S;
    S -> S i / { o = i; };
  }
}
```

Delay for synchronous automata

```montiarc
component Delay<T> {
  port <<sync>> in T i;
  port <<sync, delayed>> out T o;
  
  <<sync>> automaton {
    initial state S;
    S -> S / { o = i; };
  }
}
```

this delay can then be added anywhere in the communication circle.

```montiarc
Delay<Type> delay;
sub1.o -> sub2.i;
sub2.o -> delay.i; 
delay.o -> sub3.i;
sub3.o -> sub1.i;
```