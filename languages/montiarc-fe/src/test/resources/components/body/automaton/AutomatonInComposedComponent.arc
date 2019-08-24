/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton;

import components.body.automaton.BumpThroughPut;

/**
 * Invalid model.
 * Component components may not have automata.
 *
 * @implements [Wor16] MU2:Each atomic component contains at most one
 *  behavior model. (p. 55. Lst. 4.6) TODO: Review
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
