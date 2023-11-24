/* (c) https://github.com/MontiCore/monticore */
package variables;

import Types.Direction;

/**
 * Atomic component with two output ports and multiple parameters. Its behavior
 * is defined through an automaton and can be configured via the parameters.
 */
component PTransitions(Direction p1,
                       Direction p2,
                       Direction p3,
                       Direction p4) {

  port out Direction o1, o2;

  /**
   * The automaton sends the provided parameter as message.
   */
  <<sync>> automaton {
    initial state A;
    state B;

    // transition to B, emit p3 and p4
    A -> B / { o1 = p3; o2 = p4;};

    // transition to A, emit p1 and p2
    B -> A / { o1 = p1; o2 = p2;};
  }
}
