/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

/*
 * Use of invariants in a hierarchical automaton. All invariants should hold
 * as invariants for substates are evaluated with their superstate in mind.
 */
component InvariantValidation2 {

  int v1 = 0;
  int v2 = 0;

  port
    in int i,
    out int o;

  automaton {
    // The invariant should hold as the initial action is executed beforehand
    initial { v1 = 10; }
    state S1 [v1 == 10] {
      // The invariant should hold as the initial action is executed beforehand
      initial { v2 = 11; }
      state S11 [v2 == 11] {
        entry / { o = 11; }
      };
    };

    S1 -> S2 [i == 2] i;
    S1 -> S21 [i == 21] i;
    S1 -> S22 [i == 22] i;

    // The invariant should hold as the transition action is executed beforehand
    state S2 [v1 == 20] {
      entry / { v1 = 20; }
      initial state S21 [v2 == 21] {
        entry / { v2 = 21; o = 21; }
      };
      state S22 [v2 == 22] {
        entry / { v2 = 22; o = 22; }
      };
    };

    S2 -> S1 [i == 1] i / { v1 = 10; };
    S2 -> S21 [i == 21] i;
    S2 -> S22 [i == 22] i;
  }
}
