package automaton.invalid;

import automaton.valid.C;

component ImplementationInNonAtomicComponent {

  port
    in Integer a,
    out Integer b;
    
  component C;
  
  connect a -> c.i;
  connect c.o -> b;
  
  automaton A {
    state Idle;
    
    initial Idle;  
  }

}