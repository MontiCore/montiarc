package contextconditions.invalid;

import contextconditions.valid.BumpThroughPut;

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