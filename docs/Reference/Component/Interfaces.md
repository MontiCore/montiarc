---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Component Interface or Ports

Ports make up the interface of a component. They are the only point of interaction a component has with its environment.

Ports are declared within the bodies of the components to which they belong.
It is possible to define multiple ports within one statement.
One can also split the declaration over multiple statements.
This allows the textual grouping of conceptually related ports.

The definition of a port contains the following concepts:

* The port's [*timing*](../Concepts/Timing.md): Either *event* based or *sync*, where event is the default.
* The port's *direction*: Either it is *incoming* or *outgoing*.
    - Through incoming ports, a component *receives* data from its environment.
    - Through outgoing ports, a component *sends* data to its environment.
* The port's *type*.
    - The kind of information that flows through this port. See [Type system](../Types/index.md) for more information about types.
*  The port's *name* that is later used to refer to the port and the information flowing through it.

For example, the following component has 5 ports.

```montiarc
component MyComp {
  port sync in int inPort1,
            in int inPort2,
       sync out int outPort1,
            out int outPort2;

  port out boolean outPortInSeparateDeclaration;
}
```

Where each port has the following properties:

| Name                         | Timing | Direction | Type      |
| ---------------------------- | ------ | --------- | --------- |
| inPort1                      | sync   | incoming  | `int`     |
| inPort2                      | event  | incoming  | `int`     |
| outPort1                     | sync   | outgoing  | `int`     |
| outPort2                     | event  | outgoing  | `int`     |
| outPortInSeparateDeclaration | event  | outgoing  | `boolean` |

If multiple ports share the same properties, but only the names differ, there is an even shorter notation:

```montiarc
component MyComp {
  port in int inPort1, inPort2;
}
```

## Delayed Ports


## Complete syntax
```montiarc
port (<<delayed>>)? (sync)? <direction> <type> <name> ;
```
