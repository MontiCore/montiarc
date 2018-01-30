package component.body.automaton;

import contextconditions.valid.BumpThroughPut;

/**
 * Invalid model. Component components may not have automata.
 */
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