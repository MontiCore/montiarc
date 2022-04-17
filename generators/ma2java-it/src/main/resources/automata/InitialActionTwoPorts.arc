/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Atomic component with two output ports. Its behavior is defined through an
 * automaton. The automaton should send initial messages over its output
 * channels.
 */
component InitialActionTwoPorts {

  port out OnOff o1, o2;

  /**
   * The automaton sends messages alternating between ON and OFF.
   */
  automaton {
    // initial action, emit OFF
    initial { o1 = OnOff.OFF; o2 = OnOff.OFF; } state A;
    state B;

    // transition to B, emit ON
    A -> B / { o1 = OnOff.ON; o2 = OnOff.ON; };

    // transition to A, emit OFF
    B -> A / { o1 = OnOff.OFF; o2 = OnOff.OFF; };
  }
}
