package component.body.automaton;

import contextconditions.valid.BumpThroughPut;

component AutomatonInComposedComponent {

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