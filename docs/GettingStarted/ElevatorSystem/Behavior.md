<!-- (c) https://github.com/MontiCore/monticore -->
# Behavior
As we have seen in the ["Hello, World!"](../HelloWorld.md) model, atomic components can have behavior definitions.
The behavior is defined using [statecharts](../../Reference/Behavior/Automata.md). In the following, you will see how we can use ports to react to the environment and send messages ourselves.

In our elevator simulation, we use simplified behaviors and just interact with the console.

## Receiving

Our `Door` component has two states, open and closed. Whenever we receive a message on `shouldOpen` we react to it by changing states if needed.

=== "Door.arc"
    ```montiarc
    --8<-- "applications/tutorial/main/montiarc/elevator/Door.arc:2"
    ```

This automaton contains some new concepts. The transitions now have a guard and trigger.
The guard can be identified by the rectangular brackets `[]` and contains a Boolean expression.
The transition can only be taken if the condition inside the guard holds, i.e. evaluates to true.
Following the guard is the trigger. Transition can only be taken if their trigger is activated. In this case, if a message on the `shouldOpen` port has been received.

## Sending and Storing values

In the `Door` component, we have already seen how we can write stateful behaviors. In addition to multiple states, a behavior can also store information inside component fields.
These can then be accessed in transitions and allow for another form of stateful behaviors.

Since all the motor's ports are synchronous, the motor only uses tick transitions, too. A tick indicates one progress in time and is regularly triggered. 
If it has an up command, the motor moves the elevator up; if it has a down command, it moves down.
Otherwise, it stays in the same position. The motor always outputs its current position.

!!! info "Hint"
    Inside epsilon transitions, we can only read from synchronous ports and not event ports. Since they only have a value once an event has been received.

=== "Motor.arc"
    ```montiarc
    --8<-- "applications/tutorial/main/montiarc/elevator/Motor.arc:2"
    ```

We model this using a combination of if statements and component fields. Fields can store persistent information over the lifetime of a component.
They have a type and name followed by a mandatory default value. In our case, the motor's current position starts at `0` as set in:
```montiarc
double currentPosition = 0;
```

## Initial, Entry, Exit, Do

Finally, we have our controller, which is the heart of the system and provides most of the logic behind it.
It reacts to the button presses, decides in which direction to move the elevator, and is responsible for opening and closing the door. 

=== "ControlStation.arc"
    ```montiarc
    --8<-- "applications/tutorial/main/montiarc/elevator/ControlStation.arc:2"
    ```

The component uses all the features you have previously seen. In addition, we have initial, entry, exit, and do actions at states.

Since the `motorCommand` port is [sync delayed](./Interfaces.md#delayed-ports), we need to provide an initial value for it.
In our example, this means the motor is initially not moving.

```montiarc
initial state Init {
  entry / {
    motorCommand = MotorCMD.STOP;
  }
}
```


The OpenDoor state shows the entry, exit, and do actions.
```montiarc
state OpenDoor {
  entry / {
    openDoor = true;
  }
  do / {
    motorCommand = MotorCMD.STOP;
  }
  exit / {
    openDoor = false;
  }
}
```
These are executed, as the name suggests, when we enter the state, stay in it, or leave it, respectively.
In the context of the elevator, we open the door when we enter the state and close it when we leave it.
When this state is active, the motor is always stopped.

!!! info
    Note that exit and entry actions are also executed when a transition stays at the same state:
    ```montiarc
    OpenDoor -> OpenDoor;
    ```

## Hierarchical States

To avoid triggering exit and entry actions again when processing new incoming button presses we use hierarchical states.

States can be composed of other states. Where the innermost initial state is visited when the state is reached.

```montiarc
state OpenDoor {
  initial state IdleOpenDoor;

  IdleOpenDoor -> IdleOpenDoor requestOnFloor / {
    pendingRequests.add(requestOnFloor);
  }
  IdleOpenDoor -> IdleOpenDoor [!pendingRequests.isEmpty() && targetFloor.isEmpty()] / {
    int next = pendingRequests.first();
    pendingRequests.remove(next);
    targetFloor = Optional.of(next);
  }
}
```

For a complete look into hierarchical statecharts see [Modeling with UML](https://mbse.se-rwth.de/book1/index.php?c=chapter5-3#x1-1250005.3.2).

---

A detailed behavior reference can be found [here](../../Reference/Behavior/index.md)
