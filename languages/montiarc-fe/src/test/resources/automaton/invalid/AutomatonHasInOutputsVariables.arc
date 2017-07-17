package automaton.invalid;

component AutomatonHasInOutputsVariables {
  port
    in Integer a,
    out Integer b;

  automaton AutomatonHasInOutputsVariables {
  	variable int c;
  	input int a; // io-inputs not allowed in maa
  	output int d; // io-outputs no allowed in maa
  
  	state Start;
    initial Start;

    Start -> Start [c < 2];
  }
}
