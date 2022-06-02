/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Atomic component with an input and output port. Its behavior is defined
 * through an automaton. As outputs of automata are generally only available
 * as input in the next computation cycle, this component effectively delays
 * received inputs by one computation cycle (The implementation of MA2Java
 * enforces strong causality).
 */
component Delay {

  port in OnOff i;
  port out OnOff o;

  /**
   * The automaton delays and then forwards messages.
   */
  automaton {
    // initial state to delay initial output
    initial state S;

    // emit received message
    S -> S [ i == OnOff.ON ] / { o = OnOff.ON; };
    S -> S [ i == OnOff.OFF ] / { o = OnOff.OFF; };
  }
}
