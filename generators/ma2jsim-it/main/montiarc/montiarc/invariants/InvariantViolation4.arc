/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

/*
 * Use of invariants in a hierarchical automaton. The invariants are violated
 * as transition and entry actions and do-activities set the wrong values.
 */
component InvariantViolation4 {

  int v = 0;

  port
    in int i,
    out int o;

  automaton {
    initial state S1 [v >= 10 && v < 20] {
      entry / {
        v = 10;
      }
      initial state S11 [v == 11] {
        entry / {
          v = 11;
          o = 11;
        }
      }
    }

    S1 -> S2 [i == 2] i;
    S1 -> S21 [i == 21] i;
    S1 -> S22 [i == 22] i;

    state S2 [v >= 20 && v < 30] {
       entry / { v = 20; }
      // The invariant of the substate is violated
      // as the entry action sets the wrong value
      // before the invariant is evaluated
      initial state S21 [v == 21] {
        entry / { o = 21; }
      }
      // The invariant of both the super- and substate are violated
      // as the entry action sets the wrong value
      // before the invariants are evaluated
      state S22 [v == 22 || v == -22] {
        entry / { v = -22; o = 22; }
      }
    }

    S1 -> S3 [i == 3] i / { v = 30; }
    S1 -> S31 [i == 31] i / { v = 30; }
    S1 -> S32 [i == 32] i / { v = -32; }

    state S3 [v >= 30 && v < 40] {
      // The invariant of the substate is violated
      // as the transition action sets the wrong value
      // before the invariant is evaluated
      initial state S31 [v == 31] {
        entry / { o = 31; }
      }
      // The invariant of both the super- and substate are violated
      // as the transition actions set the wrong value
      // before the invariants are evaluated
      state S32 [v == 32 || v == -32] {
        entry / { o = 32; }
      }
    }

    S1 -> S4 [i == 4] i;
    S1 -> S41 [i == 41] i;

    state S4 [v >= 40 && v < 50] {
      entry / { v = 40; }
      // The invariant of the substate is violated
      // as the do-activity sets the wrong value
      // before the invariant is evaluated
      initial state S41 [v == 41] {
        entry / { v = 41; o = 41; }
        do / { v = 40; }
      }
    }

    S1 -> S5 [i == 5] i;
    S1 -> S51 [i == 51] i;

    state S5 [v >= 50 && v < 60] {
      entry / { v = 50; }
      do / { v = 50; }
      // The invariant of the substate is violated
      // as the do-activity sets the wrong value
      // before the invariant is evaluated
      initial state S51 [v == 51] {
        entry / { v = 51; o = 51; }
      }
    }

    S1 -> S6 [i == 6] i;

    state S6 [v >= 60 && v < 70] {
      entry / { v = 60; }
      // The invariant of both the super- and substate are violated
      // as the do-activity sets the wrong value
      // before the invariants are evaluated
      initial state S61 [v == 61] {
        entry / { v = 61; o = 61; }
        do / { v = -61; }
      }
    }
  }
}
