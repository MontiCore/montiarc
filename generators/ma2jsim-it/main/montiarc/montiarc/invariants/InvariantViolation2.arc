/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

/*
 * Use of invariants in an automaton. The invariants are violated as transition
 * and entry actions set the wrong values.
 */
component InvariantViolation2 {

  int v = 0;

  port
    in int i,
    out int o;

  automaton {
    initial { v = 1; }
    state S1 [v == 1] {
      entry / { o = 1; }
    };

    S1 -> S2 [i == 2] i;

    // The invariant is violated as the entry action sets the wrong
    // value before the invariant is evaluated
    state S2 [v == 2] {
      entry / { v = -2; o = 2; }
    };

    S1 -> S3 [i == 3] i / { v = -3; };

   // The invariant is violated as the transition action sets the wrong
   // value before the invariant is evaluated
    state S3 [v == 3] {
      entry / { o = 3; }
    };

    S1 -> S4 [i == 4] i;

    // The invariant of is violated as the do-activity sets the wrong value
    // before the invariant is evaluated
    state S4 [v == 4] {
      entry / { v = 4; o = 4; }
      do / { v = -4; }
    };
  }
}
