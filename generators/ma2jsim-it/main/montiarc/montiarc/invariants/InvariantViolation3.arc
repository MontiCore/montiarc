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
    initial state Init1 {
      entry / {
         v1 = 10;
      }
    }
    state S1 [v1 == 10] {
      entry / { o = 10; }

      initial state Init2 {
        entry / {
          v2 = -11;
        }
      }
      state S11 [v2 == 11] {
        entry / { o = 11; }
      }
    }

    Init1 -> S1;
    Init2 -> S11;
  }
}
