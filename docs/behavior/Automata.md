<!-- (c) https://github.com/MontiCore/monticore -->

Automata are a means to model behavior in MontiArc. An automaton consists of 
states and transitions between these states. When time progresses, or when the 
component receives an input, the automata may execute a transition, producing 
some output given current input and state.

An automaton is defined inside the body of a component and consists of states 
and transitions.

```montiarc
component Comp {
  automaton {
    // the state and transitions are defined here
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
S1 -> S2 [CONDITION] EVENT / { ACTION };
```

where 

* `S1` is the name of the source state (reference)

* `S2` is the name of the target state (reference)

* `[CONDITION]` (optional) is the guard where the `CONDITION` is a boolean 
expression

* `EVENT` (optional) is the event that triggers the transition. The name of 
any input port is a valid trigger. If no event trigger is specified, then the 
transition is triggered by discrete time progress.

* `/ { ACTION }` (optional) the actions that are executed when taking the 
transition where `ACTION` is a list of statements.

## Entry- and Exit-Actions

A state may define entry and exit actions that are executed when entering 
respectively exiting the state. A state with both entry and exit actions 
looks like 

```montiarc
state S {
  entry / { ACTION1 }
  exit / { ACTION2 }
};
```

where `ACTION1` and `ACTION2` are each a list of statements.

## Initial-Action

An initial state may define an initial action that is executed when entering 
the state for the very first time at the start of a run of the automaton. 
Therefore, the initial action is executed at most once during a run of an 
automaton. An initial state with an initializer action looks like 

```montiarc
initial { ACTION } state S;
```

where `{ ACTION }` is the initial action and `ACTION` a list of statements.


## Hierarchical States

A state may be hierarchically decomposed into substates. A state can have any 
number of substates and substates themselves can be hierarchically decomposed.

```montiarc
state HS {
  state Sub1 {
    state SubSub1;
    state SubSub2;  
  };
  state Sub2; 
};
```

where 

* `HS` is the name of the state

* that consists of two substates named `Sub1` and `Sub2` 

* state `Sub1` consists of two substates named `SubSub1` and `SubSub2` 

Hierarchical states and their substates can be the source and target of 
transitions just like regular states.

## Message Events

A message event occurs when an incoming, non-synchronous port receives a 
message. The execution of a transition can be triggered by such an event. 
The type and name of the message event is the type and name of the 
corresponding port.

Given the port declaration

```
port in int number;
```

the transition 

```
S1 -> S2 [CONDITION] number / { ACTION };
```

is executed when the port `number` receives a message (an `int`) and if the 
automaton is currently in state `S1` and if the `CONDITION` evaluates to `true`.

The transition's guard can then reason about properties of the received message 
and use the message in transition action.

```
S1 -> S2 [number != 0] number / { long v = 2 / number; };
```

## Time Events

A time event, which we also call a tick, occurs at discrete points in time. 
It is not specified how much time passes between two ticks, but it can be 
assumed that any calculation can be computed between two successive ticks.

If a transition does not specify a message event, it is implicitly triggered by 
a time event. The transition's trigger can also be modeled explicitly through 
the `Tick` event.

Given the port declarations

```
port <<sync>> in int a;
port <<sync>> in int b;
```

the transitions 

```
S1 -> S2 [CONDITION] / { ACTION };
S1 -> S2 [CONDITION] Tick / { ACTION };
```

are equivalent and executed at discrete points in time and if the automaton 
then is currently in state `S1` and if the `CONDITION` evaluates to `true`.

Synchronous ports are synchronized at time events. The current message on each 
synchronous port is available for the time event. A transition triggered by a 
time event can reason about properties of messages on all incoming, synchronous 
ports and use the messages in the transition action.

```
S1 -> S2 [b != 0] / { long v = a / b; };
```

## Output 

An automaton can send a message via an outgoing port by assigning it some value.
While messages on incoming ports are only available in the context of a 
specific event, messages can be sent via an outgoing port in any action. 

Given the port declarations

```
port in int number;
port out long result;
```

and the transition

```
S1 -> S2 [number != 0] number / { result = 2 / number; };
```

a message (the result of the expression `2 / number`) is sent via port `result` 
every time the transition is executed. 

The moment a message is sent via a port it is no longer available to the 
automaton (don't read from outgoing ports). Multiple messages can be sent via 
outgoing ports in quick succession. 

Given the transition

```
S1 -> S2 [number != 0] number / { 
  result = 2 / number; 
  result = 2 * number;
};
```

two message are send via port `result` every time the transition is executed. 
The messages are send in order, that is, first the result of the expression 
`2 / number` and then the result of the expression `2 * number`.

## Examples

### Event Automata

```montiarc 
import montiarc.types.Color;
import montiarc.types.ButtonEvent;

component TrafficLight {
  port in ButtonEvent reqL;
  port in ButtonEvent reqR;
  port out Color carLight;
  port out Color pedLight;
  
  automaton {
    // The light is green for the cars initially
    initial { 
      carLight = Color.GREEN;
      pedLight = Color.RED; 
    } state Green;
    
    state Yellow;
    state Red;
   
    // A pedestrian presses the button on either side of the road, 
    // tell the cars to stop (car light becomes yellow)
    Green -> Yellow reqL / {
      carLight = Color.Yellow;
    };
    Green -> Yellow reqR / {
      carLight = Color.Yellow;
    };
    
    // After some time, the car light becomes red and the light for the 
    // pedestrians becomes green
    Yellow -> Red / {
      carLight = Color.RED;
      pedLight = Color.Green; 
    };
    
    // After some time, the cars are allowed to drive again. The car light 
    // becomes green, the pedestrian light becomes red
    Red -> Green / {
      carLight = Color.Green;
      pedLight = Color.Red;
    };
  }
}
```

### Time-Synchronous Automata

Synchronous automata operate on synchronous inputs. Where message events only 
remain on the port for the duration of the event, messages on synchronous ports 
are simulations available at time progress. 

```montiarc
component Divide {
  port <<sync>> in int a, b,
       <<sync>> out int r;
  port <<sync>> out boolean of;

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
    };

    S -> S [b == 0] / {
      // send the previous result
      r = r_pre;
      // send error
      of = false;
    };
  }
}
```
