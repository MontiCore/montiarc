---
icon: material/elevator-passenger
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->
# Modeling Your First System

Until now, we have modeled a simple function, but MontiArc is **not** a programming language. Instead, it is used to model distributed systems.
In this chapter, you will learn how to create and connect different parts of a system.

## :material-elevator-passenger: The Elevator System
We will now model a simplified elevator system. It consists of:

1. A control base station that handles all the logic of where to send the elevator next
2. The elevator itself, which contains doors that can be opened and closed
3. Buttons that call the elevator to a specific floor. These are not part of the system but provide inputs to it
4. A motor for lifting and lowering the elevator

Create a new empty project, delete the HelloWorld component, and create an elevator package in the main sources.
```bash
montiarc create MyElevatorSystem
```

!!! note "Get the completed system"
    If you don't want to follow along step by step, you can get the finished elevator system by running:
    ```bash
    montiarc create MyElevatorSystem -t ElevatorTutorial
    ```

First, let's create six components inside our new elevator package.

=== "ElevatorSystem.arc"
    ```montiarc
    package elevator;

    component ElevatorSystem {
      Elevator elevator;
      ControlStation control;
      Motor motor;
    }
    ```
=== "Elevator.arc"
    ```montiarc
    package elevator;
    
    component Elevator {
      Door door;
    }
    ```
=== "ControlStation.arc"
    ```montiarc
    package elevator;
    
    component ControlStation {
    }
    ```
=== "Door.arc"
    ```montiarc
    package elevator;
    
    component Door {
    }
    ```
=== "Motor.arc"
    ```montiarc
    package elevator;
    
    component Motor {
    }
    ```

The initial description made it clear that we have to use [decomposition](../../Reference/Component/Decomposition.md) since the system consists of multiple nested parts.
We have one component called the `ElevatorSystem` that contains all the other components. Components can be instantiated with `Type name;` where the type refers to another component.

Having decomposition alone is not enough; we want these components to interact with one another, and for that, we need interfaces.
