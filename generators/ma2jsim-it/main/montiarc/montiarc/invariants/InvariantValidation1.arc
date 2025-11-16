/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

/*
 * Use of invariants in an automaton. Ensures that invariants can operate
 * on component variables. All invariants should hold as invariants are
 * evaluated after initial, transition, and entry actions.
 */
component InvariantValidation1 {

  int v = 1;

  port
    in int i,
    out int o;

  automaton {
    initial state S1 [v == 1] {
      entry / { o = 1; }
    }

    S1 -> S1 [i == 1] i;
    S1 -> S2 [i == 2] i;
    S1 -> S3 [i == 3] i / {
      v = 3;
    }

    // The invariant should hold as the entry action is executed beforehand
    state S2 [v == 2] {
      entry / { v = 2; o = 2; }
    }

    S2 -> S1 [i == 1] i;
    S2 -> S2 [i == 2] i;
    S2 -> S3 [i == 3] i / {
      v = 3;
    }

    // The invariant should hold as the transition action is executed beforehand
    state S3 [v == 3] {
      entry / o = 3;
    }

    S3 -> S1 [i == 1] i;
    S3 -> S2 [i == 2] i;
    S3 -> S3 [i == 3] i;
  }
}
