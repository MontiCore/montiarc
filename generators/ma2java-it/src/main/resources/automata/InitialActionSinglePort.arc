/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Atomic component with a single output port. Its behavior is defined through
 * an automaton. The automaton should send an initial message over its single
 * output channel.
 */
component InitialActionSinglePort {

  port out OnOff o;

  automaton {
    // initial action, emit OFF
    initial { o = OnOff.OFF; } state A;
    state B;

    // transition to B, emit ON
    A -> B / { o = OnOff.ON; };

    // transition to A, emit OFF
    B -> A / { o = OnOff.OFF; };
  }
}
