<!-- (c) https://github.com/MontiCore/monticore -->
# Connectors

Connectors connect the interfaces (ports) of components, defining the flow of 
messages and determining which components communicate with each other. 
Connectors are defined in the body of the component type. A connector looks 
like 

```montiarc
SOURCE -> TARGET;
```

where 

* `SOURCE` is the (qualified) name of the source port (reference)

* `TARGET` is the (qualified) name of the target port (reference)

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

## Compatibility

Ports can be connected if the direction, timing, and type are compatible.

#### Direction
The source of a connector can be:

* an incoming port of the component

* an outgoing port of a subcomponent

The target of a connector can be

* an outgoing port of the component

* an incoming port of a subcomponent

We distinguish three kinds of connectors:

* A connector from an incoming port of the component to an incoming port of a 
subcomponent, forwarding messages received by the component to one of its
subcomponents. E.g., `i -> sub.j; ` forwards message received by port `i` to 
port `j` of subcomponent `sub`.

* A connector from an outgoing port of a subcomponent to an outgoing port of 
the component, forwarding messages sent by the subcomponent. E.g., `sub.o -> p;` 
forwards messages sent by subcomponent `sub` on port `o` via port `p`.

* A connector from an outgoing port of a subcomponent to an incoming port of 
a subcomponent (potentially the same subcomponent), sometimes called a hidden 
connector. E.g., `sub1.o -> sub2.i;` connects port `o` of subcomponent `sub1` 
to port `i` of subcomponent `sub2`.

#### Timing
Regarding [timing](../Concepts/Timing.md), two rules apply:

- The source of an event port can be any other timing.
- The source of a sync port has to be sync as well.

#### Types
[Types](../Types/index.md) are compatible if the target type is a subtype of the source type. 

## Feedback

If subcomponents form a communication circle along the direction of connectors, 
then a subcomponent may communicate with itself, either directly or indirectly 
across other subcomponents. We call this a feedback loop. 

In a direct feedback loop, the output of a component is directly connected to 
the input of the component. The component communicates directly with itself.

```montiarc
sub.o -> sub.i; 
```

In an indirect feedback loop, the output of a component is connected to the 
input of the component indirectly across one to multiple subcomponents. The 
component communicates indirectly with itself.

```montiarc
sub1.o -> sub2.i;
sub2.o -> sub3.i;
sub3.o -> sub1.i;
```

Communication is abstracted to be instantaneous. However, for the propagation 
of timing events in feedback loops, we need delay. Otherwise, the component's output at some point in time would depend on itself. 

Where the delay happens in the communication circle is irrelevant, just there
needs to be some kind of delay. 

Delay can be introduced directly on the [behavior](../Behavior/index.md) definition of an atomic component by marking it [delayed](../Behavior/Delay.md).
Or using explicit delay components that delay all messages they receive. As part of the [standard language library](../../Libraries/StandardLibrary/Components.md), these components are available in all models.

=== "Event-based"
    ```montiarc
    --8<-- "libraries/montiarc-base/main/montiarc/montiarc/lang/Delay.arc:2"
    ```
=== "Synchronous"
    ```montiarc
    --8<-- "libraries/montiarc-base/main/montiarc/montiarc/lang/TSDelay.arc:2"
    ```

These delay components can then be added anywhere in the communication circle during [decomposition](./Decomposition.md):

```montiarc
Delay<Type> delay;
sub1.o -> sub2.i;
sub2.o -> delay.i; 
delay.o -> sub3.i;
sub3.o -> sub1.i;
```