package components.body.automaton.states;

/**
 * Invalid model.
 * Each automaton must provide an initial state.
 *
 * @implements [Wor16] AC4: The automaton has at least one initial state.
 *  (p. 99, Lst. 5.14)
 */
component AutomatonHasNoInitialState {

  port
    in String x;
    
    
  automaton A {
    state B;
  }


}