<!-- (c) https://github.com/MontiCore/monticore -->

Components can be decomposed to structure a system, separate concerns, and 
reduce complexity by distributing system functionality across multiple smaller 
components. 

In MontiArc, component types define the decomposition of components in terms 
of subcomponents and connectors between the components' interfaces. 

## Component Declaration

Subcomponents are declared in the body of a component type, specifying the 
subcomponent's type and defining its name. A simple subcomponent declaration 
looks like 

``` 
TYPE SUB;
```

where 

* `TYPE` is the subcomponent's (qualified) type (reference) 

* `SUB`  is the subcomponent's unique name (defining)

Subcomponents are directly instantiated alongside there declaration. 

A component type can have of multiple subcomponents of different types but also 
multiple subcomponents of the same type. For convenience, multiple subcomponents
of the same type can be instantiated by stating their names in a comma-separated 
list after the component type, which looks like 

``` 
TYPE SUB, SUB1;
```

and is a shorthand notation for 

``` 
TYPE SUB;
TYPE SUB1;
```

## Connectors

Connectors connect the interfaces (ports) of components, defining the flow of 
messages and determining which components communicate whit each-other. 
Connectors are defined in the body of the component type and look like 

`SOURCE` -> `TARGET`;

where 

* `SOURCE` is the (qualified) name of the source port (reference)

* `TARGET` is the (qualified) name of the target port (reference)

Generally, there are three types of connectors:

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

```
sub.o -> sub.i; 
```

In an indirect feedback loop the output of a component is connected to the 
input of the component indirectly across one to multiple subcomponents. The 
component communicates indirectly with itself.

```
sub1.o -> sub2.i;
sub2.o -> sub3.i;
sub3.o -> sub1.i;
```

While communication is otherwise abstracted to be instantaneous, for the 
propagation of timing events in feedback loops we need delay. Otherwise, the 
output to some point in time `t` would depend on itself. 

Where the delay happens in the communication circle is irrelevant, just there
needs to be some kind of delay. 

Delay can be introduced through the stereotype `<<delayed>>` on the output 
port of an atomic component, specifying outputs on that port are delayed by 
one Tick. For simplicity, we can also introduce a specific delay: 

```
component Delay<T> {
  port in T i;
  port <<delayed>> out T o;
  
  automaton {
    initial state S;
    S -> S / { o = i; };
  }
}
```

this delay can then be added anywhere in the communication circle.

```
Delay<Integer> delay;

sub1.o -> sub2.i;
sub2.o -> delay.i; 
delay.o -> sub3.i;
sub3.o -> sub1.i;
```