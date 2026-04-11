/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: The automaton declares multiple states with the same name.
 * States must be unique in the whole state space.
 */
component DuplicateStates2 {

  automaton {
    initial state s1 {
      state s3;
    }

    state s2 {
      state s3 {
        state s4;
      }
    }

    s1 -> s3;
    s2 -> s3;
    s4 -> s3;
    s3 -> s1;
    s3 -> s2;
    s3 -> s4;
  }
}
