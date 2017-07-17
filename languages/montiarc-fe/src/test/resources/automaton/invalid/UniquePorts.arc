package automaton.invalid;

component UniquePorts {
  port
    in Integer a,
    out Integer a;
  
  automaton UniquePorts {  
  	state Start;
    initial Start;

    Start -> Start;
  }
}
