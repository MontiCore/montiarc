/* (c) https://github.com/MontiCore/monticore */
package automata.nondeterminism;

/**
 * Atomic component with ambiguous state transitions.
 */
component Transition {

  port <<sync>> in int i;
  port <<sync>> out int o;

  automaton {
    initial state start;
    state s1;
    state s2;
    state s3;
    state s4 {
      initial state s4_1;
    };
    state s5 {
      initial state s5_1;
    };
    state end1;
    state end2;

    // case 1: same guard
    start -> s1 [ i == 1 ] / { o = 1; };
    s1 -> end1 [ i == 1 ] / { o = 1; };
    s1 -> end2 [ i == 1 ] / { o = 2; };

    // case 2: same guard and target
    start -> s2 [ i == 2 ] / { o = 2; };
    s2 -> end2 [ i == 2 ] / { o = 2; };
    s2 -> end2 [ i == 2 ] / { o = -2; };

    // case 3: guard or no guard
    start -> s3 [ i == 3 ] / { o = 3; };
    s3 -> end1 [ i == 3 ] / { o = 1; };
    s3 -> end2 / { o = 2; };

    // case 4: hierarchy parent first
    start -> s4 [ i == 4 ] / { o = 4; };
    s4 -> end1 [ i == 4 ] / { o = 1; };
    s4_1 -> end2 [ i == 4 ] / { o = 2; };

    // case 5: hierarchy child first
    start -> s5 [ i == 5 ] / { o = 5; };
    s5_1 -> end1 [ i == 5 ] / { o = 1; };
    s5 -> end2 [ i == 5 ] / { o = 2; };
  }
}