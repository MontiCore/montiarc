---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->
# Connectors

Interfaces alone are not enough. Interconnections are needed to specify the flow of information.
[Connectors](../../Reference/Component/Connectors.md) between ports can only be used in [decomposed](../../Reference/Component/Decomposition.md) components.
A port can be connected to multiple other ports of [compatible](../../Reference/Component/Connectors.md#compatibility) direction, type, and timing.

Since the elevator and the system as a whole are the only decomposed components, we only need to add connectors in these. 
First, we forward the `openDoor` information from the elevator to the door. 

=== "Elevator.arc"
    ```montiarc
    --8<-- "applications/tutorial/main/montiarc/elevator/Elevator.arc:2"
    ```

A connector has its source on the left and target on the right. If we want to connect a subcomponent's port, we use a format of `subcomponentName.portName`.

Our overall system contains more than one connection. We connect all subcomponents using the corresponding ports.

=== "ElevatorSystem.arc"
    ```montiarc
    --8<-- "applications/tutorial/main/montiarc/elevator/ElevatorSystem.arc:2"
    ```

With the connectors present, we can clearly see why the controller has to have a [delayed port](./Interfaces.md#delayed-ports). The connectors build a circle, also called a [feedback loop](../../Reference/Component/Connectors.md#feedback), where the inputs depend on the outputs.

In the last step, we will add behavior to the remaining components, giving us a system that we can simulate and interact with.

---

A detailed decomposition reference can be found [here](../../Reference/Component/Decomposition.md)