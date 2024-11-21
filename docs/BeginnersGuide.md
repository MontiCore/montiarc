<!-- (c) https://github.com/MontiCore/monticore -->

MontiArc is a textual architecture description language. A MontiArc component 
model describes a topology of components and connections between these 
components.

## Component type

A component type describes a set of components with common characteristics and 
defines their interface, structure, and behavior. A component type definition 
looks like

```montiarc
package car.ui;

component WindowController {
  // Component body
}
```

where 

* `WindowController` is the unique name of the component type

## Comments 

Comments can be used to explain a model in more detail and will be ignored by
the parser.

A single-line comment starts with two forward slashes (`//`) and ends with the 
end of line. 

A multi-line comments starts with `/*` and ends with `*/`.

## Packages and Imports

A component type is located in some package. Elements (component types, data 
types) defined in the same package are usually visible to each other and can 
therefore can be used in other models. Elements located in another package 
must be imported explicitly. 

A component model with a package declaration and some imports looks like

```montiarc
package car.ui;

import car.ui.window.WindowPosition;
import car.ui.window.WindowButtonMoveEvent;

component WindowController {
  // Component content
}
```

where 

* `package car.ui;` is a package declaration that defines that the component 
type `WindowController` is located in package `car.ui`

* `import car.ui.window.WindowPosition;` is an import statement that imports
element `car.ui.window.WindowPosition` so that it can be used in the component 
model via its simple name `WindowPosition`.

## Project file tree organization

Each component model is located in a textfile. The file extension is `.arc`. 
The name of the file must correspond to the name of the component type. The 
files relative path from the source directory must conform to the package 
declaration.

That is, the file contained the component model

```montiarc
package car.ui;

component WindowController {
  // Component content
}
```

is called `WindowController.arc` and located in the relative directory `car/ui`.

## Component interfaces

Components encapsulate their internals and interact with components in their 
environment via well-defined interfaces. A component's interface is made up of 
ports and each port has a direction, a type, and a unique name. A component's 
ports are defined in the body of the corresponding component type. 

For example 

```montiarc
// Package declaration and imports ...

component WindowController {
  port in WindowPosition position,
       in WindowButtonMoveEvent buttonEvent,
       in FingerProtectionOrder fingerProtection,
       out WindowMoveAction moveAction;
}
```

defines that components of type `WindowController` have three incoming ports 
called `position`, `buttonEvent` and `fingerProtection` of type`WindowPosition`, 
`WindowButtonMoveEvent`, and`FingerProtectionOrder` respectively, as well as an 
outgoing port called `moveAction` of type `WindowMoveAction`.

## Data types

Components communicate by sending and receiving messages. Messages are received
via incoming ports and send via outgoing ports. Messages are also of some type, 
which defines the message's structure. A message's type is called a data type 
and can be defined in a classdiagram. 

For our example, we define the following data types.

```cd4code
package car.ui;

classdiagram window {

  public enum WindowPosition {
    OPEN, CLOSED, INBETWEEN;
  }

  public enum WindowButton {
    UP, DOWN;
  }

  public enum WindowMoveAction {
    MOVE_UP, MOVE_DOWN, STOP_MOVEMENT;
  }
  
  public enum FingerProtectionOrder {
    PROTECT, PROTECTION_OFF;
  }
}
```

* the data type `WindowPosition` indicates the window's position. A window 
can be `OPEN`, `CLOSED`, or `INBETWEEN`.

* the data type `WindowButton` indicates whether the `UP` or `DOWN` button was
pressed.

* the data type `WindowMoveAction` indicates the direction the window should 
be moving. A window can move up (`MOVE_UP`), down (`MOVE_DOWN`) or stop 
moving (`STOP_MOVEMENT`).

* the data type `FingerProtectionOrder` indicates the state of the finger 
protection. The finger protection can be turned on (`PROTECT`) or be turned off
( `PROTECTION_OFF`).

## Automata 

Automata define the discrete state space and behavior of a component. 
An automaton consists of states and transitions between these states. Events
(incoming message, time progress) trigger the execution of a transition and the 
actions executed alongside the transition determine which messages are send in
response via outgoing channels. 

```montiarc
package car.ui;

import car.ui.window.WindowButton;
import car.ui.window.WindowPosition;
import car.ui.window.WindowMoveAction;
import car.ui.window.FingerProtectionOrder;

component WindowController {

  port <<sync>> in WindowPosition position,
       in WindowButton buttonEvent,
       in FingerProtectionOrder protectFinger,
       <<sync>> out WindowMoveAction winMoveAction;

  automaton {
    initial state Closed;
    state Intermediate;
    state Open;
    state Blocked;

    // Constantly send outputs via the synchronized output port
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
    Open -> Blocked [protectFinger == FingerProtectionOrder.PROTECT] protectFinger;
    Intermediate -> Blocked [protectFinger == FingerProtectionOrder.PROTECT] protectFinger;
    Closing -> Blocked [protectFinger == FingerProtectionOrder.PROTECT] protectFinger;

    // Open the window when the button `DOWN` is pressed
    Closed -> Opening [buttonEvent == WindowButton.DOWN] buttonEvent;

    // Stop the window's motor if the window is fully open
    Opening -> Open [position == WindowPosition.OPEN];

    // Close the window when the button `CLOSED` is pressed
    Open -> Closing [buttonEvent == WindowButton.UP] buttonEvent;

    // Stop the window's motor if the window is fully closed
    Closing -> Closed [position == WindowPosition.CLOSED];

    // Stop the window's movement and remain in the current position
    // when the window is currently closing and the button `DOWN` is released
    Closing -> Intermediate [buttonEvent == WindowButton.DOWN] buttonEvent;
    // when the window is currently opening and the button `OPEN` is released
    Opening -> Intermediate [buttonEvent == WindowButton.UP] buttonEvent;

    // Resume the window's movement
    Intermediate -> Opening [buttonEvent == WindowButton.DOWN] buttonEvent;
    Intermediate -> Closing [buttonEvent == WindowButton.UP] buttonEvent;

    // Recovering from finger protection
    Blocked -> Intermediate [
      protectFinger == FingerProtectionOrder.PROTECTION_OFF
    ];
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
  In our case, we defined all logic that controls the `winMoveAction` using entry / exit / and do actions: When entering and remaining in the `Opening` / `Closing` state, the window move action is always sent accordingly.
  Moreover, when exiting these states, the information that the window movement should stop is sent once.

### Transition definitions
They follow the syntax `<source-state> -> <target-state> [ <condition> ] <event> (/ <action>;)`, even if we have not seen the `/ <action>` syntax in the example.
Important elements of the syntax are:
* Within square brackets, one can define the condition under which the transition is triggered.
  In our case, we constrain the values of input ports.
* One can also define actions that should be executed when the transition is triggered.
  While in the example above we defined all the behavior using entry-, do-, and exit-actions, we could alternatively use transition actions the following way:
  ```montiarc
  // excerpt within automaton { ... }:
  Closed -> Opening [buttonEvent == WindowButton.DOWN] buttonEvent / {
    winMoveAction = WindowMoveAction.MOVE_DOWN;
  };
  // Self-loop for sending continuous signals
  Opening -> Opening [buttonEvent != WindowButton.UP] buttonEvent / {
    winMoveAction = WindowMoveAction.MOVE_DOWN;
  }
  Opening -> Open [position == WindowPosition.OPEN] buttonEvent / {
    winMoveAction = WindowMoveAction.STOP_MOVEMENT;
  };
  ```
  However, in our example, this has the disadvantage that every transition that ends in the `Opening` state must also set the `winMoveAction` to `MOVE_DOWN` and that every transition exiting the `Opening` state must set the `winMoveAction` to `STOP_MOVEMENT`.
  As this is more verbose, we decided to use the entry-, do-, and exit-actions in our example.
  Still it is important to know both concepts, as the transition actions can express behavior that entry-, do- and exit-actions can not.

### Interacting with the environment: port values
Moreover, we can generally see that
* Values of incoming ports can be read by using their name in an expression, e.g.: `buttonEvent == WindowButton.UP`.
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
  package car.ui;

  import car.ui.window.WindowButton;

  component HumanMachineInterface {
    port out WindowButton buttonEvent;

    // The component behavior implementation is not relevant to us.
    // The same holds for the other components.
  }
  ```
* A _finger protection sensor_ that recognizes whether a finger is in the gap of a window
  ```montiarc
  package car.sec;

  component FingerProtectionSensor {
    port out FingerProtectionOrder order;
  }
  ```
* A _window position sensor_ that recognizes the current state of the window (open, closed, or in between)
  ```montiarc
  package car.window;

  component WindowPositionSensor {
    port <<sync>> out WindowPosition winPosition;
  }
  ```
* A _window motor_ that, when prompted, moves the window
  ```montiarc
  package car.window;

  component WindowMotor {
    port in WindowMoveAction moveOrder;
  }
  ```
* A _status LED icon_ in the cockpit of the car that gives the user feedback by indicating whether current window movement takes place and in which direction
  ```montiarc
  package car.ui;

  component WindowStatusLED {
    port in WindowMoveAction moveOrder;
  }
  ```

### The composition of the sub systems
Connecting these systems to the `WindowController`, creating the overall `WindowSystem` looks like the following:
```montiarc
package car.window;

import car.sec.FingerProtectionSensor;
import car.ui.HumanMachineInterface;

component WindowSystem {
  WindowController controller;

  WindowPositionSensor winPositionSensor;
  HumanMachineInterface hmi;
  FingerProtectionSensor fingerSensor;

  winPositionSensor.winPosition -> controller.position;
  hmi.winButtonEvent -> controller.buttonEvent;
  fingerSensor.order -> controller.protectFinger;

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
For example, take the declaration `fingerSensor.order -> controller.protectFinger;` within `WindowSystem`:
The finger detection sensor has some internal logic that at some point detects a finger in the window gap. It then sends the instruction through its outgoing `order`-port that the window shall be locked.
This information (a port value of `FingerProtectionOrder.PROTECT`) becomes the new current value at the `protectFinger`-port of the window controller.
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
  fingerSensor.order -> controller.protectFinger;
  
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
