/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Atomic component with a single output port. Its behavior is defined through
 * an automaton. The automaton should send messages over its single output
 * channel even though it cannot read from input channels.
 */
component Source {

  port out OnOff o;

  /**
   * The automaton sends messages alternating between ON and OFF.
   */
  automaton {
    initial state A;
    state B;

    // transition to B, emit ON
    A -> B / { o = OnOff.ON; };

    // transition to A, emit OFF
    B -> A / { o = OnOff.OFF; };
  }
}
