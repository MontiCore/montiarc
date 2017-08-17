package contextconditions.invalid;

import contextconditions.valid.BumpThroughPut;

component ImplementationInNonAtomicComponent {

  port
    in Integer a,
    out Integer b;
    
  component BumpThroughPut c;
  
  connect a -> c.i;
  connect c.o -> b;
  
  automaton A {
    state Idle;
    
    initial Idle;  
  }

}