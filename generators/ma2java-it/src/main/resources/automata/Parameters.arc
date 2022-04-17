/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.Direction;

/**
 * Atomic component wit a two parameters and output ports. Its behavior is
 * defined through an automaton. The parameters configure the output.
 */
component Parameters(Direction p1, Direction p2) {

  port out Direction o1;
  port out Direction o2;

  /**
   * The automaton emits the provided parameters as messages.
   */
  automaton {
    // initial action, emit p1 and p2
    initial { o1 = p1; o2 = p2; } state S;

    // emit p1 and p2 on every computation cycle
    S -> S / { o1 = p1; o2 = p2; };
  }
}
