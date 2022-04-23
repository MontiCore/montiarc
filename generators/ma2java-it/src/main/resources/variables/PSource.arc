/* (c) https://github.com/MontiCore/monticore */
package variables;

import types.Direction;

/**
 * Atomic component with a single output port and parameter. Its behavior is
 * defined through an automaton and can be configured via the parameter.
 */
component PSource(Direction p) {

  port out Direction o;

  /**
   * The automaton emits the provided direction every computation cycle.
   */
  automaton {
    initial {
      o = p;
    } state A;

    // emit p
    A -> A / { o = p; };
  }
}
