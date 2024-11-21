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
  }
  state Sub2; 
}
```

where 

* `HS` is the name of the state

* that consists of two substates named `Sub1` and `Sub2` 

* state `Sub1` consists of two substates named `SubSub1` and `SubSub2` 

Hierarchical states and their substates can be the source and target of 
transitions just like regular states.

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
