/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'a1' and 'a2' referenced in the entry actions
 * of various states are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInEntryAction4 {

  automaton {
    initial state S1 {
      entry / { a1 = 0; }
    }

    state S2 {
      entry / { a2 = 0; }
      state S3 {
        entry / { a3 = 0; }
      }
    }
  }
}
