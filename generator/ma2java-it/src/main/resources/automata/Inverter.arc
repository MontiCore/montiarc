/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Atomic component with an input and output port. Its behavior is defined
 * through an automaton. The inverter inverts the received command.
 */
component Inverter {

  port in OnOff i;
  port out OnOff o;

  /**
   * The automaton inverts messages.
   */
  automaton {
    // initial state to delay initial output
    initial state S;

    // emit received message
    S -> S [ i == OnOff.ON ] / { o = OnOff.OFF; };
    S -> S [ i == OnOff.OFF ] / { o = OnOff.ON; };
  }
}
