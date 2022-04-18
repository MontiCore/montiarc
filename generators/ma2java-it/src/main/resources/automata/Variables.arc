/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.Direction;

/**
 * Atomic component wit a component variables. Its behavior is defined through
 * an automaton. The variables are assigned values via parameters and local
 * declarations. The automaton may read from and write to component variables.
 */
component Variables(Direction p1) {

  Direction v1 = Direction.RIGHT;
  Direction v2 = v1;
  Direction v3 = v2;
  Direction v4 = p1;

  port in Direction i;
  port out Direction o;

  /**
   * An automaton may read from and write to component variables.
   */
  automaton {
    // initial action, emit RIGHT
    initial { o = v1; } state S;

    // assign v1, emit FORWARDS, initial assignment has no effect
    S -> S [ i == Direction.FORWARDS ] / { v1 = Direction.FORWARDS; o = v1; };

    // assign v2, emit BACKWARDS, initial assignment has no effect
    S -> S [ i == Direction.BACKWARDS ] / { v2 = Direction.BACKWARDS; o = v2; };

    // assign v3, emit LEFT, initial assignment has no effect
    S -> S [ i == Direction.LEFT ] / { v3 = Direction.LEFT; o = v3; };

    // assign v4, emit RIGHT, initial assignment to p1 has no effect
    S -> S [ i == Direction.RIGHT ] / { v4 = Direction.RIGHT; o = v4; };
  }
}
