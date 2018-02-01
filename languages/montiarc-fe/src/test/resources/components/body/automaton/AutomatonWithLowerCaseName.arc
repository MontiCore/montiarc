package components.body.automaton;

/**
 * Invalid model. Automata must have an name staring upper case.
 */
component AutomatonWithLowerCaseName {
  port
    in Integer a;
    
  int c;
  
  automaton lowerCaseName {
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
