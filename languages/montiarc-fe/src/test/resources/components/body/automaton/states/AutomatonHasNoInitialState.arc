package components.body.automaton.states;

/**
* @implements [Wor16] AC4: The automaton has at least one initial state. (p. 99, Lst. 5.14)
* Invalid model: embedded automaton has no initial state.
*/
component AutomatonHasNoInitialState {

  port
    in String x;
    
    
  automaton A {
    state B;
  }


}