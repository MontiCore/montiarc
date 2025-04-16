<!-- (c) https://github.com/MontiCore/monticore -->
# Interfaces

Interfaces provide well-defined means for communication between components. The interface of a component is composed of [ports](../../Reference/Component/Interfaces.md).
Every port has a direction, type, and [timing](../../Reference/Concepts/Timing.md).

Incoming ports receive messages, whereas outgoing ports send messages. The port type can be primitive or an object type.
We look at user-defined type definitions [later](./Types.md).

## Event Ports

The default timing of ports is event-based; this means events are emitted and received over ports.

In our elevator system, we start with the buttons. They should send an event once a button has been pressed and on which floor.
To model this, we use an outgoing port of the primitive type integer. Each value represents a floor that the elevator has been called to.

=== "Buttons.arc"
    ```montiarc
    package elevator;
    
    component Buttons {
      port out int pressedOnFloor;
    }
    ```
Ports are always declared with the port keyword followed by their direction, type, and name.

Similarly to this, we add ports for the elevator and door. Where events are received when the door state should change.
=== "Elevator.arc"
    ```montiarc
    package elevator;
    
    component Elevator {
      port in boolean openDoor;
      
      Door door;
    }
    ```
=== "Door.arc"
    ```montiarc
    package elevator;
    
    component Door {
      port in boolean shouldOpen;
    }
    ```

## Sync Ports

An alternative timing is synchronous. Here, all ports send and receive values at the same time.

Another interesting example is the motor component. It should receive commands to go up, down, or stay where it's at, but also send out its current position.
For that, we use two ports, one incoming and one outgoing.

=== "Motor.arc"
    ```montiarc
    package elevator;
    
    component Motor {
      port sync in MotorCMD command;
      port sync out double position;
    }
    ```

For now, just know that object ports behave the same as primitive ports, and we will look at the `MotorCMD` type definition [later](./Types.md). 


## Delayed Ports
Since in both timings it is possible that the outputs causally depend on the inputs, we can mark a port as delayed.

In the case of the control station, this means the `motorCommand` is dependent on the `motorPoition`, but the `motorPosition` depends on the `motorCommand`.
To avoid this circle, the `motorCommand` is delayed and sent only after the current time step has been completed.  

=== "ControlStation.arc"
    ```montiarc
    package elevator;
    
    component ControlStation {
      port in int requestOnFloor;
      port out boolean openDoor;
      port sync in double motorPosition;
      port <<delayed>> sync out MotorCMD motorCommand;
    }
    ```

A delayed port can be identified by the `<<delayed>>` stereotype.

---

A detailed interface reference can be found [here](../../Reference/Component/Interfaces.md)