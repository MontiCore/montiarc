package component.body.automaton;

/**
 * Invalid model. Automata must provide an initial state.
 */
component AutomatonWithoutInitialState {

    port 
      in int a,
      out int b;
    
    automaton AutomatonWithoutInitialState {
        state S;   
    }
}