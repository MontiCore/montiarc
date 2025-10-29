/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

/*
 * Use of invariants in a hierarchical automaton. The invariant of the substate
 * of the initial state is violated as the initial action of the substate sets
 * the wrong value.
 */
component InvariantViolation3 {

  int v1 = 0;
  int v2 = 11;

  port
    in int i,
    out int o;

  automaton {
    initial { v1 = 10; }
    state S1 [v1 == 10] {
      entry / { o = 10; }
      // The invariant is violated as the initial action sets the wrong value
      // before the invariant is evaluated
      initial { v2 = -11; }
      state S11 [v2 == 11] {
        entry / { o = 11; }
      }
    }
  }
}
