package components.body.automaton;

/**
 * Invalid model. Automata must have an name staring upper case.
 * @implements [Wor16] AC6: The names of automata start with capital letters. (p. 101, Lst. 5.16)
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
