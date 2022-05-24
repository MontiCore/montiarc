<!-- (c) https://github.com/MontiCore/monticore -->
# MontiArc

## The MontiArc Architecture Description Language

In MontiArc, architectures are topologies of component and connectors in which 
autonomously acting components perform computations. Connectors define the
interaction between the components' interfaces, which consist of typed,
directed ports. Components are either atomic or composed of connected
subcomponents. Atomic components yield behavior descriptions defined via
integration of handcrafted code. For composed components,
the behavior emerges from the behavior of their subcomponents.


## The Warehouse Example

A growing company has been managing its warehouse manually since it was formed
several years ago. Several workers drove forklifts all day to move stored items
around. Automation of the storage system was long overdue and has finally been
approved by the management. Thus, the warehouse has been prepared for being
automated. Everything in stock is stored in standardized containers suitable
for handling with machines. Further, the floor plan, and accordingly the layout
of the storage racks, now conforms to a simple three-dimensional coordinate
system. Thus, every stored object can be assigned a unique position with
just three coordinates.

The automation system should use transporters to move objects on ground level.
These transporters need to be able to hold more than one object for efficiency.
They will be complemented by cranes which are able to grab an object from
higher up on the shelves and put it down onto a transporter. These two should
work together seamlessly for a more efficient storage management. To this
effect, a controlling component coordinating a transporter and a crane is
required. To access the storage, a request has to be submitted. Therefore, a
software component handling these request is also necessary. This component
should take in requests and prepare them to be processed by the system.

For a first draft, the warehouse management system should be modeled together
with software representations of the transporters and cranes. It will include
models for the transporter, the crane, a controller for the transporter
and the crane, and a component to prepare requests.

The package *factory.warehouse* contains components which should be used in the
warehouse system.\
To recap:
 * The crane can grab objects and move them around in  three-dimensional space.
   It has very slow horizontal movement when holding an object.
 * The transporter, which can move along the floor quickly, even if loaded, is
   to be used. It must be able to carry multiple objects at once, therefore
   having some sort of container.
 * The request manager coordinates a crane and a transporter, allowing us to
   move objects relatively quickly (get them down from the shelves with a
   crane, put them on a transporter, move them along the horizontal plane,
   pick them up at their destination via crane). It will also handle 
   preparing the incoming requests.

The WarehouseManager component should unite these three components in
one System and is the source of requests for a request manager.

### Your Task
Your first task will be to add interfaces to the Components `Transporter` and
`Crane` (packages *`factory.warehouse.transporter`* and *`factory.warehouse`*
respectively) and to add a `Container` instance to the `Transporter` component.
When creating the interfaces, consider the following:
 * the `Transporter` needs a `Container` instance as subcomponent and therefore
   has to have the corresponding ports. Make sure to take a look at the code
   for the `Container` component to add the correct ports and connectors.
 * the `RequestManager` needs to be able to tell both components from where to
   where they should be moving
 * both components need to be able to accept an object (`StorageObject` in
   class diagram `factory.warehouse.cd`) and need to be able
   to pass the object/objects they are holding to another component
 * it might be beneficial for the `RequestManager` to know:
   * where the `Transporter` and `Crane` are (for more efficient routing)
   * how much capacity the `Transporter` has left (as to not overload it)
   * if the `Transporter` and `Crane` are currently moving due to a request
     or if they are idle (as they should not stay idle for long)

Secondly, you will have to complete the interface of the `WarehouseManager`,
check if all subcomponents that should be instantiated, are actually being
instantiated, and add appropriate connectors.

It will not be your task to add any kind of behavior to these components,
since that would go beyond the scope of this tutorial.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

