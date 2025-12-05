/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'g1', 'g2', and 'g3' referenced in guards of
 * various transitions are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInGuard4 {

  automaton {
    initial state S1 {
      S1 -> S1 [g1];
    }

    state S2 {
      S2 -> S1 [g2];
      state S3 {
        S3 -> S1 [g3];
      }
    }
  }
}
