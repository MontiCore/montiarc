/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Atomic component with a single input port. Its behavior is defined through
 * an automaton. The automaton should change states with respect to messages
 * received over its input channel.
 */
component Sink {

  port <<sync>> in OnOff i;

  /**
   * The automaton transition between states based on the input received.
   */
  automaton {
    initial state A;
    state B;

    // transitions to B if i == ON
    A -> B [ i == OnOff.ON ];

    // transitions to A if i == OFF
    B -> A [ i == OnOff.OFF ];
  }
}
