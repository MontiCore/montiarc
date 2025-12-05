/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'a1', 'a2', and 'a3' referenced in actions of
 * various transitions are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInTransitionAction6 {

  automaton {
    initial state S1 {
      S1 -> S1 / { a1 = 0; }
    }

    state S2 {
      S2 -> S1 / { a2 = 0; }
      state S3 {
        S3 -> S1 / { a3 = 0; }
      }
    }
  }
}
