<!-- (c) https://github.com/MontiCore/monticore -->
# Automata (or actually: Statecharts)

Automata are a means to model behavior in MontiArc. An automaton consists of 
states and transitions between these states. When time progresses, or when the 
component receives an input, the automata may execute a transition, producing 
some output given current input and state.

An automaton is defined inside the body of a component and consists of states 
and transitions.

???+ example
    ```montiarc
    component Comp {
      port in int i;

      automaton {
        // the state and transitions are defined here
        initial state S1;
        state S2;

        S1 -> S2 / {}
        S2 -> S1 [i > 0] i / {}
      }
    }
    ```

## States

An automaton has at least one state and exactly one initial state.

A simple state declaration looks like

```montiarc
initial state S;
```

where

* `initial` is the optional modifier defining the initial state, 

* `S` is the unique name of the state (defines the state's name)

## Transitions

An automaton can have any number of transitions between states.

A simple transition looks like

```montiarc
S1 -> S2 [CONDITION] EVENT / { ACTION }
```

where 

* `S1` is the name of the source state (reference)

* `S2` is the name of the target state (reference)

* `[CONDITION]` (optional) is the guard where the `CONDITION` is a boolean 
expression

* `EVENT` (optional) is the event that triggers the transition. The name of 
any input port is a valid trigger. If no event trigger is specified, then the 
transition is triggered by discrete time progress.

* `/ { ACTION }` (optional) the [actions](./Automata.md#actions) that are executed when taking the 
transition.

!!! info "Hint" 
    If a transition does **not** specify an event trigger (i.e., no port name is written after the guard or at all), then this transition is called an _epsilon transition_ or _time-triggered transition_. It is executed at each simulation time step.

## Actions

`ACTION`s describe the reaction to a stimulus. They may be added to a state as entry- and/or exit-action or to a transition as an action. 
`ACTION`s are a list of [statements](../Concepts/Statements.md) that are executed when a state is entered, left, or when a transition is taken.

### Entry and Exit Actions

A state may define entry and exit actions that are executed when entering exiting the state, respectively.
A state with both entry and exit actions looks like 

```montiarc
state S {
  entry / { ACTION1 }
  exit / { ACTION2 }
}
```

where `ACTION1` and `ACTION2` are each a list of statements.

In this example the input port is shadowed when entering the state `S`. After assigning `0` to `i`, the value of `i` (`= 0`) is written on port `o`. 
```montiarc
component Comp {
  port in int i;
  port out int o; 
  automaton { 
    initial state S { 
      entry / { 
        int i = 0; 
        o = i; 
      }
    }
  }
}
```

### Further Action Examples

!!! info "Note"
      The `ACTION`s described below, which are executed when a transition is taken, can **also** be used in entry and exit actions. 
      Note also that this is only a collection of examples that are intended to provide a general overview and to serve as a starting point.

`ACTION`s can be used for writing values on ports,
```montiarc
component Comp {
  port in int i;
  port out int o;
  
  automaton {
    initial state S;
    S -> S [i > 1] / { o = 42; }
  }
}
```

Including loops,
```montiarc
component Comp {
  port in int i;
  port out int o;

  automaton {
    initial state S;
    S -> S [i == 1] / { 
      for (int j = 0; j <= 10; j++) { 
         o = j; 
      } 
    }
  }
}
``` 

Assigning values to variables,
```montiarc
component Comp {
  port in int i;
  port out int o;
  
  boolean b;
  
  automaton {
    initial state S;
    S -> S [i == 1] / { b = true; }
  }
}
``` 

or for Executing functions
```montiarc
component Comp {
  port in int i;
  port out int o;
  
  automaton {
    initial state S;
    S -> S [i == 1] / { foo.someMethodCall(); }
  }
}
``` 


## Hierarchical States

A state may be hierarchically decomposed into substates. A state can have any 
number of substates and substates themselves can be hierarchically decomposed.

```montiarc
state HS {
  state Sub1 {
    state SubSub1;
    state SubSub2;  
  }
  state Sub2; 
}
```

where 

* `HS` is the name of the state

* that consists of two substates named `Sub1` and `Sub2` 

* State `Sub1` consists of two substates named `SubSub1` and `SubSub2` 

Hierarchical states and their substates can be the source and target of 
transitions just like regular states.

## Message Events

A message event occurs when an incoming, non-synchronous port receives a 
message. The execution of a transition can be triggered by such an event. 
The type and name of the message event is the type and name of the 
corresponding port.

Given the port declaration

```montiarc
port in int number;
```

the transition 

```montiarc
S1 -> S2 [CONDITION] number / { ACTION }
```

is executed when the port `number` receives a message (an `int`) and if the automaton is currently in state `S1` and if the `CONDITION` evaluates to `true`.

The transition's guard can then reason about the properties of the received message and use the message in the transition action.

```montiarc
S1 -> S2 [number >= 0] number / { int v = number; }
S1 -> S2 [number < 0] number / { int v = -number; }
```

## Time Events (Epsilon Transitions)

A time event, which we also call a tick, occurs at discrete points in time. 
It is not specified how much time passes between two ticks, but it can be 
assumed that any calculation can be computed between two successive ticks.

If a transition does not specify a message event, it is implicitly triggered by a time event, it is what we call epsilon transition.

The transition 

```montiarc
S1 -> S2 [CONDITION] / { ACTION } // for synchronous automata
```

is executed at discrete equidistant points in time, i.e., whenever a time slice finishes. 
Of course, as usual, the automaton has to be in state `S1` and the `CONDITION` evaluates to `true`.
However, no explicit incoming trigger is needed; only the assumed internally clock issues a 'TICK'.

Given the port declarations

```montiarc
port sync in int a;
port sync in int b;
```

Synchronous ports are synchronized at time events.
The current message on each synchronous port is available for the time event.
A transition triggered by a time event can reason about properties of messages on all incoming, synchronous ports, and use the messages in the transition action.

```montiarc
S1 -> S2 [b != 0] / { int v = a / b; }
```

## Output 

An automaton can send a message via an outgoing port by assigning it some value.
While messages on incoming ports are only available in the context of a specific event, 
messages can be sent via an outgoing port in any action. 

Given the port declarations

```montiarc
port in int number;
port out long result;
```

and the transition

```montiarc
S1 -> S2 [number != 0] number / { result = 100 / number; }
```

a message (the result of the expression `100 / number`) is sent via port 
`result` every time the transition is executed. 

The moment a message is sent via a port, it is no longer available to the automaton (don't read from outgoing ports). 
Multiple messages can be sent via outgoing ports in quick succession. 

Given the transition

```montiarc
S1 -> S2 [number != 0] number / { 
  result = 100 / number; 
  result = 100 * number;
}
```

Two messages are sent via port `result` every time the transition is executed. 
The messages are sent in order, that is, first the result of the expression 
`100 / number` and then the result of the expression `100 * number`.

## Examples
We show examples for each time interpretation, although they can also be mixed.

### Event Automaton

```montiarc 
import montiarc.types.Color;
import montiarc.types.ButtonEvent;

component TrafficLight {
  port in ButtonEvent reqL;
  port in ButtonEvent reqR;
  port out Color carLight;
  port out Color pedLight;
  
  automaton {
    // When entering this state, the light gets green for the cars and red for the predestrians
    initial state Green {
      entry / {
        carLight = Color.GREEN;
        pedLight = Color.RED; 
      }
    };
    
    state Yellow;
    state Red;
   
    // A pedestrian presses the button on either side of the road, 
    // tell the cars to stop (car light becomes yellow)
    Green -> Yellow reqL / {
      carLight = Color.YELLOW;
    }
    Green -> Yellow reqR / {
      carLight = Color.YELLOW;
    }
    
    // After some time, the car light becomes red and the light for the 
    // pedestrians becomes green
    Yellow -> Red / {
      carLight = Color.RED;
      pedLight = Color.GREEN; 
    }
    
    // After some time, the cars are allowed to drive again. 
    Red -> Green;
  }
}
```

### Time-Synchronous Automaton

```montiarc
component Divide {
  port sync in int a, b,
       sync out int r;
  port sync out boolean of;

  int r_pre = 0;

  automaton {
    initial state S;

    S -> S [b != 0] / {
      // store the result
      r_pre = a / b;
      // send the result on port r
      r = r_pre;
      // no error, send ok
      of = true;
    }

    S -> S [b == 0] / {
      // send the previous result
      r = r_pre;
      // send error
      of = false;
    }
  }
}
```
