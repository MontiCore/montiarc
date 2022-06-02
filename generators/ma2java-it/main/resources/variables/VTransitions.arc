/* (c) https://github.com/MontiCore/monticore */
package variables;

import types.Direction;

/**
 * Atomic component wit a single input port and two output ports. Its behavior
 * is defined through an automaton. The component features variables which the
 * automaton may read from and write to.
 */
component VTransitions {

  Direction v1 = Direction.LEFT;
  Direction v2 = Direction.RIGHT;
  Direction v3 = v1;
  Direction v4 = v2;

  port in Direction i;
  port out Direction o1, o2;

  /**
   * An automaton may read from and write to component variables.
   */
  automaton {
    // initial action, emit RIGHT
    initial { o1 = v1; o2 = v2; } state S;

    // assign v1, emit FORWARDS, initial assignment has no effect
    S -> S [ i == Direction.FORWARDS ] / { o1 = v1; v1 = Direction.FORWARDS; o2 = v1; };

    // assign v2, emit BACKWARDS, initial assignment has no effect
    S -> S [ i == Direction.BACKWARDS ] / { o1 = v2; v2 = Direction.BACKWARDS; o2 = v2; };

    // assign v3, emit LEFT, initial assignment has no effect
    S -> S [ i == Direction.LEFT ] / { o1 = v3; v3 = Direction.LEFT; o2 = v3; };

    // assign v4, emit RIGHT, initial assignment to p1 has no effect
    S -> S [ i == Direction.RIGHT ] / { o1 = v4; v4 = Direction.RIGHT; o2 = v4; };
  }
}
