<!-- (c) https://github.com/MontiCore/monticore -->

This guide introduces you to modeling with MontiArc.
It is especially suited, if you have not worked with MontiArc before.
If you just want to review concepts, then look at the dedicated pages.
To follow along, also make sure to read Getting Started.
It introduces you to available tooling that enables you to verify the correctness of written models.

## Basic model structure
Let's start by looking at how to write models:
MontiArc models are written in plain text.
The content of a model file generally looks like the following:

```montiarc
package car.userinteraction;

import car.userinteraction.window.WindowPosition;

component WindowController {
  // Component content
}
```
The structure is as follows:
1. Models are organized in *package structures*.
   Elements defined in the same package are usually visible to each other.
   The first declaration of a model defines in which package the model lays.
2. To use model elements that are defined in other packages, *import statements* are used.
   In the example, we import the `WindowPosition` type.
   We will see later what this means and for what it is used.
3. Every model file contains one top-level *component type* definition.
   The definition starts with the keyword `component`. 
   It is followed by the *name* that is mandatory so that we can refer to the component from other locations within our models.
4. The last syntactic element of the example is a simple comment, declared using double slashes `//`.
   Everything that follows within the same line will be ignored by MontiArc.

## Project file tree organization
Now that we have seen what the content of the file is, we will investigate the role of the file in the file system structure of the project.

The file extension is `.arc`.
The file system path to models must match the packages in which they are defined.
For our `WindowController` example, the location could look like `my-project/src/montiarc/car/userinteraction/WindowController.arc`.
For MontiArc, it is only relevant that the last part of the the path matches the whole package hierarchy (namely, `car/userinteraction/WindowController.arc`).
However, build tools may constrain the rest of the path.

## Component interfaces
An important concept of MontiArc is that the internals of a component are hidden to its operating environment and regarding the other direction: that the component is agnostic of its operating environment, so that it can be used in various operational scenarios.
However, somehow the component has to interact with the environment.
*Ports* are used for that role.
Their concept includes the following aspects:
<!--They appear in models in the following way:-->
* They have predefined directions: Either they are *incoming* or *outgoing*.
   If a port is incoming, then the owning component uses the port to *receive* data from its environment.
   On the other hand, if the port is outgoing, then the owning component uses the port to *send* data to its environment.
* Ports are *typed*.
  The types of a ports constrains *what* data can be received / sent through them.
  Example port types are `int`, `boolean`, or `WindowPosition` (from the example above).
  We will look at which types can be used later.
*  *Names* are assigned to each port so that we can later refer to them and the information flowing through them.

### Example for component interfaces

Let us now look at how we can declare ports for components.
As an example, let us assume that we are developing a window motor control system for a car.
It gets three different kinds of information from its environment:
* Whether a window *button* is pressed and in which direction,
* Whether a *finger* has been detected within in the gap of the window,
* The *position* of the window.
   The controller uses this information to stop the window motor when it has been fully closed or opened to avoid damage to the glass.

Based on this information, the window control system should send *out* a concrete *motor control command* to the motor.

We can model this environment interaction in MontiArc like this:
```montiarc
// Package declaration and imports ...

component WindowController {
  port in WindowPosition position,
       in WindowButtonMoveEvent buttonEvent,
       in FingerProtectionOrder fingerProtectionOrder,
       out WindowMoveAction winMoveAction;
}
```
The declaration of the component's ports is started with the `port` keyword.
It is followed by a comma-separated list that contains the declaration of the individual ports.
Every such declaration has the form `<direction> <port-type> <port-name>`.
Note the semicolon ending the port declaration.
It is also allowed to split the declaration of all ports into separate statements:
```montiarc
// Package declaration and imports ...

component WindowController {
  port in WindowPosition position;
  port in WindowButtonMoveEvent buttonEvent;
  
  port in FingerProtectionOrder fingerProtectionOrder,
       out WindowMoveAction winMoveAction;
}
```
You may use these different options to structure your component model and group ports into  groups of related functionality.
The last example is a bad one in that sense, as the `fingerProtectionOrder` port is grouped with the `winMoveAction` port where no direct coherence exists (at least not a stronger one than with the other ports).

## Defining component behavior with automatons
After having looked at the API of a component to its environment, let's explore how we can define how the component internally works: let's define some behavior.

### The behavior we want to model
We will work again with the example of the `WindowController` for which we have already defined the component interface.
We want to use this information to implement the following behavior:
* If the window button is pressed in the "up" direction, the window shall completely close.
  One press of the "up" button should suffice.
  It is not mandatory that the button is held during the whole process.
* The process is the same for the opposite direction, opening the window completely once the button is pressed in the "down" direction.
* The user can cancel the opening / closing process by pressing the window button in the direction that is opposite to the window movement ("down" while closing and "up" while opening).
* When a finger has been detected in an open window gap, than the closing window movement must stop.
  The system returns to normal operation only after receiving the signal that the finger left the window gap and consequently receiving a window "down" movement signal from the window button.
* To order the window motor to move, a movement signal shall be sent _constantly_ during the movement period.
* When stopping the window movement, the window motor should _once_ receive the information to stop the movement.

Before looking at the behavior implementation of the `WindowController`, lets look at the port types so that we know how to utilize them in our automaton.
We use class-diagram models to define the used types as enumerations.
You don't have to understand the class-diagram syntax completely, but it should give you an intuitive understanding of the enum types that we will later use.
```cd4code
// Model com/example/window.cd
package com.example;

classdiagram window {

  public enum WindowPosition {
    OPEN, CLOSED, INBETWEEN;
  }

  public enum WindowButtonMoveEvent {
    UP_PRESSED, DOWN_PRESSED;
  }

  public enum WindowMoveAction {
    MOVE_UP, MOVE_DOWN, STOP_MOVEMENT;
  }
}
```
```cd4code
// Model com/example/fingerprotection.cd
package com.example;

classdiagram fingerprotection {
  public enum FingerProtectionOrder {
    PROTECT, PROTECTION_OFF;
  }
}
```

### The MontiArc model implemented with an automaton

```montiarc
package com.example.window;

// Types within class diagram "window" are in a sub-package called window.
// As the "window" class diagram is in the package "com.example", its inner types are
// in the package com.example.window, and thereby in the same package as the WindowController.
// Hence, we do not need to import its types. We only need to import the types from the
// fingerprotection class diagram:
import com.example.fingerprotection.FingerProtectionOrder;

component WindowController {
  
  // In this example we distinguish between "continuous" and "event" values.
  // With "continuous" we mean that we expect that there is always a value at the given port.
  // With "event" we mean that there is only sometimes a value at that port.
  //   If an event value is not present at a port, then expressions like PORT_NAME == ...
  //   will always evaluate to false.
  
  port in WindowPosition position,  // Continuous value
       in WindowButtonMoveEvent buttonEvent,  // Event value
       in FingerProtectionOrder fingerProtectionOrder,  // Event value
       out WindowMoveAction winMoveAction;  // Continuous values during movement, event value when stopping

  automaton {
    initial state Closed;
    state Intermediate;
    state Open;
    state Blocked;
    state UnblockingOnDownSignal;
    
    state Opening {
      entry / winMoveAction = WindowMoveAction.MOVE_DOWN;
      do / winMoveAction = WindowMoveAction.MOVE_DOWN;
      exit / winMoveAction = WindowMoveAction.STOP_MOVEMENT;
    };

    state Closing {
      entry / winMoveAction = WindowMoveAction.MOVE_UP;
      do / winMoveAction = WindowMoveAction.MOVE_UP;
      exit / winMoveAction = WindowMoveAction.STOP_MOVEMENT;
    };


    // Blocking the movement in case of a detected finger
    Open -> Blocked [fingerProtectionOrder == FingerProtectionOrder.PROTECT];
    Intermediate -> Blocked [fingerProtectionOrder == FingerProtectionOrder.PROTECT];
    Closing -> Blocked [fingerProtectionOrder == FingerProtectionOrder.PROTECT];

    // Transitioning from closed to open
    Closed -> Opening [buttonEvent == WindowButtonMoveEvent.DOWN_PRESSED];
    Opening -> Open [position == WindowPosition.OPEN];

    // Transitioning from opened to closed
    Open -> Closing [buttonEvent == WindowButtonMoveEvent.UP_PRESSED];
    Closing -> Closed [position == WindowPosition.CLOSED];

    // Interrupting the window movement
    Closing -> Intermediate [buttonEvent == WindowButtonMoveEvent.DOWN_PRESSED];
    Opening -> Intermediate [buttonEvent == WindowButtonMoveEvent.UP_PRESSED];

    // Resuming the window movement
    Intermediate -> Opening [buttonEvent == WindowButtonMoveEvent.DOWN_PRESSED];
    Intermediate -> Closing [buttonEvent == WindowButtonMoveEvent.UP_PRESSED];

    // Recovering from finger protection
    Blocked -> UnblockingOnDownSignal [
      fingerProtectionOrder == FingerProtectionOrder.PROTECTION_OFF
    ];
    UnblockingOnDownSignal -> Open [buttonEvent == WindowButtonMoveEvent.DOWN_PRESSED
                                    && position == WindowPosition.OPEN];
    UnblockingOnDownSignal -> Intermediate [buttonEvent == WindowButtonMoveEvent.DOWN_PRESSED
                                            && position != WindowPosition.OPEN];
  }
}
```
We see that the definition of the automaton is part of the component body, next to the port definitions.
It is started with the keyword `automaton` and its content is defined in a body (`{ ... }`).
The body within the braces contains two major elements: *state* and *transition* definitions.

### State definitions
They follow the syntax `state <state-name> ;`.
Furthermore, the state definition can contain the following elements:
* Putting `initial` before the stat declaration marks a state as the one that the component will start in once instantiated.
In our case, the component starts, assuming that the window is closed.
* We can define actions that are executed
  1) once the state is entered (_entry_ actions),
  2) once it is left (_exit_ actions), and
  3) always when no other transition is taken by the state machine (_do_ actions)

  These actions are defined within a state body, enclosed by {curly braces} following the state name.
  The defined actions are formulated as statements, following `entry /`, `exit /`, and `do /`.
  If multiple actions should be executed, then they are wrapped in {curly braces}, creating a statement block. Example:
  ```montiarc
  entry / {
    foo();
    bar();
  }
  ```
  In our case, we defined all logic that controls the `windowMoveAction` using entry / exit / and do actions: When entering and remaining in the `Opening` / `Closing` state, the window move action is always sent accordingly.
  Moreover, when exiting these states, the information that the window movement should stop is sent once.

### Transition definitions
They follow the syntax `<source-state> -> <target-state> [ <condition> ] (/ <action>;)`, even if we have not seen the `/ <action>` syntax in the example.
Important elements of the syntax are:
* Within square brackets, one can define the condition under which the transition is triggered.
  In our case, we constrain the values of input ports.
* One can also define actions that should be executed when the transition is triggered.
  While in the example above we defined all the behavior using entry-, do-, and exit-actions, we could alternatively use transition actions the following way:
  ```montiarc
  // excerpt within automaton { ... }:
  Closed -> Opening [buttonEvent == WindowButtonMoveEvent.DOWN_PRESSED] / {
    winMoveAction = WindowMoveAction.MOVE_DOWN;
  };
  // Self-loop for sending continuous signals
  Opening -> Opening [buttonEvent != WindowButtonMoveEvent.UP_PRESSED] / {
    winMoveAction = WindowMoveAction.MOVE_DOWN;
  }
  Opening -> Open [position == WindowPosition.OPEN] / {
    winMoveAction = WindowMoveAction.STOP_MOVEMENT;
  };
  ```
  However, in our example, this has the disadvantage that every transition that ends in the `Opening` state must also set the `winMoveAction` to `MOVE_DOWN` and that every transition exiting the `Opening` state must set the `winMoveAction` to `STOP_MOVEMENT`.
  As this is more verbose, we decided to use the entry-, do-, and exit-actions in our example.
  Still it is important to know both concepts, as the transition actions can express behavior that entry-, do- and exit-actions can not.

### Interacting with the environment: port values
Moreover, we can generally see that
* Values of incoming ports can be read by using their name in an expression, e.g.: `buttonEvent == WindowButtonMoveEvent.UP_PRESSED`.
  In this sense they behave like read-only variables, always with the most recent port value.
* Values of outgoing ports can be set by using their name in an assignment expression, e.g.: `winMoveAction = WindowMoveAction.MOVE_UP`.
  In this sense they behave like write-only variables.
  Reading form them is not allowed.

There are also other methods to define the behavior of components.
You can read more about this at [Behavior].

## Composing different systems to a whole
Up until now, we have only seen components isolated from their environment.
But MontiArc is a _component and connector_ architecture description language.
These languages connect different components together in order to build a whole new system.
In MontiArc, this composed system results in a new component.
This means that when developing a complex architecture, individual sub systems can be developed independently as components.
When it is time to integrate the different sub systems, one just connects the different components that represent the sub systems and thereby derives the overall system.
As every component is agnostic of its environment, reusing them is also facilitated.

### The overall system we want to model
Let's see how such a composition can be defined in MontiArc!
To this end, let's extend the window control example that we already introduced.
To complete the window system, we also need the following components:
* A _human machine interface_ that captures information about the state of the window buttons.
  A car may have multiple buttons to control the same window (directly at the window, at the drivers seat, and in a remote controller).
  This human machine interfaces aggregates all the state of all potential control sources and sends out a single control source to the window controller.
  ```montiarc
  package com.example.hmi;

  import com.example.window.WindowButtonMoveEvent;

  component HumanMachineInterface {
    port out WindowButtonMoveEvent winButtonEvent;

    // The component behavior implementation is not relevant to us.
    // The same holds for the other components.
  }
  ```
* A _finger protection sensor_ that recognizes whether a finger is in the gap of a window
  ```montiarc
  package com.example.fingerprotection;

  component FingerProtectionSensor {
    port out FingerProtectionOrder order;
  }
  ```
* A _window position sensor_ that recognizes the current state of the window (open, closed, or in between)
  ```montiarc
  package com.example.window;

  component WindowPositionSensor {
    port out WindowPosition winPosition;
  }
  ```
* A _window motor_ that, when prompted, moves the window
  ```montiarc
  package com.example.window;

  component WindowMotor {
    port in WindowMoveAction moveOrder;
  }
  ```
* A _status LED icon_ in the cockpit of the car that gives the user feedback by indicating whether current window movement takes place and in which direction
  ```montiarc
  package com.example.window;

  component WindowStatusLED {
    port in WindowMoveAction moveOrder;
  }
  ```

### The composition of the sub systems
Connecting these systems to the `WindowController`, creating the overall `WindowSystem` looks like the following:
```montiarc
package com.example.window;

import com.example.fingerprotection.FingerProtectionSensor;
import com.example.hmi.HumanMachineInterface;

component WindowSystem {
  WindowController controller;

  WindowPositionSensor winPositionSensor;
  HumanMachineInterface hmi;
  FingerProtectionSensor fingerSensor;

  winPositionSensor.winPosition -> controller.position;
  hmi.winButtonEvent -> controller.buttonEvent;
  fingerSensor.order -> controller.fingerProtectionOrder;

  WindowMotor motor;
  WindowStatusLED led;

  controller.winMoveAction -> motor.moveOrder;
  controller.winMoveAction -> led.moveOrder;
}
```

The two syntactic elements that we see are _instantiations_ of components that we defined earlier and _connectors_ between ports of the component instances.

### Component instantiations
When we want to use components that we defined before, we are _instantiating_ them in an object-oriented sense:
When we defined components such as `WindowController` or `WindowMotor` before, we actually defined _component types_, blueprints that do not exhibit behavior, but only define it.
When we are using components, e.g. as parts of a bigger component, we are instantiating component types.
Such an instantiation follows the syntax `<component-type-name> <instance-name> ;`.
We are using the instance names later to establish connections among their ports.
In accordance with object-orientation, component instances are independent from each other, even if they are of the same type.
E.g., a car may have multiple `WindowSystem`s operating independently from each other:
```montiarc
component Car {
  WindowSystem winFrontLeft;
  WindowSystem winFrontRight;
  WindowSystem winBackLeft, winBackRight;
}
```
This example also highlights that multiple instances of the same type can be declared within a single statement, comma-separated.

Note that components types that are defined within the same package can be used without further ado.
If one wants to use component types from other packages, one has to import them at the beginning of a model.
This is exemplified by the `WindowSystem` importing `HumanMachineInterface` and `FingerProtectionSensor`.

### Connectors
After having declared the component instances that one wants to use, one connects their ports with _connectors_ through which information flows.
The syntax of of a connector declaration is `<source-instance-name>.<port-name> -> <target-instance-name>.<port-name> ;`.
Information that the source component sends through a port travels through the attached connector and becomes the input of the declared port of the target component.
For example, take the declaration `fingerSensor.order -> controller.fingerProtectionOrder;` within `WindowSystem`:
The finger detection sensor has some internal logic that at some point detects a finger in the window gap. It then sends the instruction through its outgoing `order`-port that the window shall be locked.
This information (a port value of `FingerProtectionOrder.PROTECT`) becomes the new current value at the `fingerProtectionOrder`-port of the window controller.
This information can then be used in the behavior implementation of the window controller.

If one connects component instances, then there are some restrictions on what ports can be connected:
* The source port of the connection must be an _outgoing_ port;
* The target port of the connection must be an _incoming_ port;
* The target port's type must be of the same type or a super-type of the source port's type.
  This is important to guarantee that only data with the correct type is arriving at the target port.
* An incoming port of any component instance can only be connected to exactly one source port.
  On the other side, an outgoing port can be connected to multiple target ports.
  We can see this in the composition of the `WindowSystem`:
  ```montiarc
  controller.winMoveAction -> motor.moveOrder;
  controller.winMoveAction -> led.moveOrder;
  ```
  The `winMoveAction`-Port of the window controller is connected to both the window motor, as well as a reporting window-movement status LED.



### Connectors among external and internal ports
The `WindowSystem` that we have investigated is an isolated system:
It is a composition without ports to the outside.
However this is not a constraint: Decomposed components can also have ports to the outside.
E.g., when deploying multiple window systems in a car, the LEDs that indicate window movement of the individual windows may be bundled into a central display in the cockpit that is not part of the window system itself anymore.
Such a change to the window system could look like the following:
```montiarc
package com.example.window;

import com.example.fingerprotection.FingerProtectionSensor;
import com.example.hmi.HumanMachineInterface;

component WindowSystem {
  // The information
  port out WindowMoveAction currentMovement;

  WindowController controller;
  
  // No more LED component in the WindowSystem!

  WindowPositionSensor winPositionSensor;
  HumanMachineInterface hmi;
  FingerProtectionSensor fingerSensor;

  winPositionSensor.winPosition -> controller.position;
  hmi.winButtonEvent -> controller.buttonEvent;
  fingerSensor.order -> controller.fingerProtectionOrder;
  
  WindowMotor motor;
  controller.winMoveAction -> motor.moveOrder;

  // There isn't any connection to the led anymore,
  // but to the outgoing 'currentMovement' port instead:
  controller.winMoveAction -> currentMovement; 
}
```

The embedding of such a window system into a car could look like the following:
```montiarc
component Car {
  WindowSystem winFrontLeft, winFrontRight, winBackLeft, winBackRight;
  CockpitDisplay display;

  winFrontLeft.currentMovement -> display.winFrontLeftMovement;
  winFrontRight.currentMovement -> display.winFrontRightMovement;
  winBackLeft.currentMovement -> display.winBackLeftMovement;
  winBackRight.currentMovement -> display.winBackRightMovement;

  // More car stuff
}
```

The changes we made to the `WindowSystem` are
* We have a new outgoing port `currentMovement`
* A connector from the controller port that dictates the window movement to the `currentMovement` port.
  As the `currentMovement` port is owned by the component type (`WindowSystem`) but not any sub component, we do not reference it with a preceding instance name within the connector declaration: `controller.winMoveAction -> currentMovement`.

There are new directional constraints when "outer" ports of component types are part of a connector:
* Incoming ports of component types may only play a role as source ports in connectors, connecting to
  * incoming ports of component instances or
  * outgoing ports of component types;
* Outgoing ports of component types may only play a role as target ports in connectors, connecting to
  * outgoing ports of component instances, or
  * incoming ports of component types.

## Conclusion
This is the end of the beginner's guide.
We have seen how to define components and their interfaces through which they interact with their environment.
Then we have seen, how we can define behavior of components using automatons.
At the end, we have seen how we can compose components to bigger systems which themselves turn out to be components!
If you want to learn more, there look at other ways to define behavior, or advanced ways in the definition of component types, such as configuring them during initialization with type or value parameters. 

## References
The running example of the window control system is based on the following case study:\
Lity, S., Lachmann, R., Lochau, M., & Schaefer, I. (2013). Delta-oriented software product line test models-the body comfort system case study. _Technical report, TU Braunschweig_.
