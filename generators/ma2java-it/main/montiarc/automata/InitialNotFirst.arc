/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Hierarchic component where the first state is not the initial state.
 */
component InitialNotFirst {

  port in int i;

  automaton {
    state A {
      initial state A2;
    };
    initial state B {
      state B1;
      initial state B2 {
        state B2_1;
        initial state B2_2;
      };
      state B3;
    };
  }
}
