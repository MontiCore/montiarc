/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'a1' and 'a2' referenced in the exit actions
 * of various states are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInExitAction4 {

  automaton {
    initial state S1 {
      exit / { a1 = 0; }
    }

    state S2 {
      exit / { a2 = 0; }
      state S3 {
        exit / { a3 = 0; }
      }
    }
  }
}
