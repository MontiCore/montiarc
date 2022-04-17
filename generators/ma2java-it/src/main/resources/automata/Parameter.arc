/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.Direction;

/**
 * Atomic component wit a single parameter and output port. Its behavior is
 * defined through an automaton. The parameter configures the output.
 */
component Parameter(Direction p) {

  port out Direction o;

  /**
   * The automaton sends the provided parameter as message.
   */
  automaton {
    // initial action, emit p
    initial { o = p; } state S;

    // emit p every computation cycle
    S -> S / { o = p; };
  }
}
