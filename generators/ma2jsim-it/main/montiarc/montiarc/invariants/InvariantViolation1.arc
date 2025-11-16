/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

/*
 * Use of invariants in an automaton. The invariant of the initial state is
 * violated as the initial action sets the wrong value.
 gr*/
component InvariantViolation1 {

  int v = 1;

  port
    in int i,
    out int o;

  automaton {
    initial state Init {
      entry / {
        v = -1;
      }
    }

    Init -> S1;

    state S1 [v == 1] {
      entry / { o = 1; }
    }
  }
}
