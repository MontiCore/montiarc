<!-- (c) https://github.com/MontiCore/monticore -->

```montiarc
component WindowSystem {
  port in WindowButtonMoveEvent windowCommand;
  port out WindowMoveAction currentMovementInfo;

  // Instantiating sub components:
  WindowController controller;
  WindowPositionSensor winPositionSensor;
  FingerProtectionSensor fingerSensor;
  WindowMotor motor;

  // Connecting an incoming port of the enclosing component to a sub component
  windowButtonCommand -> controller.command;

  // Connecting sub components among each other
  winPositionSensor.winPosition -> controller.position;
  fingerSensor.order -> controller.fingerProtectionOrder;

  // 1. Connecting one source port with multiple target ports;
  // 2. Connecting a sub component port to an outgoing port of the enclosing component
  controller.winMoveAction -> motor.moveOrder,
                              currentMovementInfo;
}
```
Components can be defined to be a composition of other components.
These decomposed components are hierarchically structured into further subcomponents and
thus have their behavior derived from the sub component's composition.

## Subcomponents
```montiarc
component WindowSystem {

  // Instantiating subcomponents:
  WindowController controller;
  WindowPositionSensor winPositionSensor;
  FingerProtectionSensor fingerSensor;
  WindowMotor motor;

  // ...
}
```

Sub components are added to a component by instantiating them within the body 
of a component type definition.
They are defined by first referencing the name of the component type to 
instantiate, followed by a name under which one can refer to the newly 
created sub component instance: `<component-type> <name> ;`.
It is also possible to instantiate multiple component instances of the 
same type by declaring their names in a comma-separated list after the 
component type, e.g.: `FooComp foo1, foo2, foo3;`

### Instantiation of generics
```montiarc
FooComp<Student, EMail> foo1, foo2, foo3;
```

### Instantiation of configurable components
```
FooComp foo1(0.314, true), foo2(4.2, false);
```


## Connectors
```montiarc
component WindowSystem {
  port in WindowButtonMoveEvent windowCommand;
  port out WindowMoveAction currentMovementInfo;

  // ...

  windowButtonCommand -> controller.command;
  winPositionSensor.winPosition -> controller.position;
  fingerSensor.order -> controller.fingerProtectionOrder;
  controller.winMoveAction -> motor.moveOrder,
                              currentMovementInfo;
}
```
Connectors connect the interfaces of components to realize component communication.
They have the syntax `<source-port> -> <target-port> ;` and obey following behavior:
* They are directed: information can only flow through them in a predefined direction 
  (implicitly set by which connected port is a source and which is a target).
  If a response is needed from a connected sub component, this has to be modeled as a 
  feedback via another communication channel
* Source ports may be
  * Incoming ports of the enclosing component
  * Outgoing ports of a sub component
* Target ports may be
  * Outgoing ports of the enclosing component
  * Incoming ports of a sub component
* A target port may only be part of _one_ connector. On the other hand, source 
ports may participate in multiple connectors.
As such, the targets of a source port can be declared within a single connector 
declaration (`source -> target1, target2, ..., targetN;`) or be split over multiple 
multiple connector declarations:
  ```montiarc
  source -> target1;
  source -> target2;
  ```
* The types of the ports participating in the connection must match.
  Alternatively, the target port may have a supertype of the source port's type.
* If the port ot the enclosing component type participates in the connection, 
one references it just by its name.
* If the port of a sub component participates in the connection, one references 
it with the instance name of the connected sub component that is followed by the 
name of the component's port. They are separated by a point: `instanceName.portName`
