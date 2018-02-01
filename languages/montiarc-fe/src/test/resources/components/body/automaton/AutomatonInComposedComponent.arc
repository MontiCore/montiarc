package components.body.automaton;

import components.body.automaton.BumpThroughPut;

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