package automaton.invalid;

component AutomatonHasInOutputsVariables {
  port
    in Integer a,
    out Integer b;

  automaton AutomatonHasInOutputsVariables {
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
