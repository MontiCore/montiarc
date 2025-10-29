/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

/*
 * Use of invariants in an automaton. The invariant of the initial state is
 * violated as the initial action sets the wrong value.
 */
component InvariantViolation1 {

  int v = 1;

  port
    in int i,
    out int o;

  automaton {
    // The invariant is violated as the initial action sets the wrong
    // value before the invariant is evaluated
    initial { v = -1; }
    state S1 [v == 1] {
      entry / { o = 1; }
    }
  }
}
