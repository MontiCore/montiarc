<!-- (c) https://github.com/MontiCore/monticore -->

```montiarc
component MyComp {
  port <<sync>> in int inPort1,
       <<timed>> in int inPort2,
                 out int outPort1,  // No timing specified
       <<untimed>> out int outPort2;

  port out boolean outPortInSeparateDeclaration;
}
```
Ports are declared within the bodies of the components to which they belong.
It is possible to define multiple ports within one statement.
One can also split the declaration over multiple statements.
This allows the textual grouping of conceptually related ports.

The definition of a port contains the following concepts:
* The port's *direction*: Either it is *incoming* or *outgoing*.
   Through incoming ports, a component *receives* data from its environment.
   Through outgoing ports, a component *sends* data to its environment.
* The port's' *type*.
  Only data of that can flow through the port. See [Type system] for more information about types.
*  The port's *name* that is later used to refer to the port and the information flowing through it.



## Complete syntax
```montiarc
port (<port-timing>) <direction> <type> <name> ;
```
