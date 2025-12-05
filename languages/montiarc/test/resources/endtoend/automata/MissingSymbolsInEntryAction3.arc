/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'a1' and 'a2' referenced in the two entry actions
 * of the state are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInEntryAction3 {

  automaton {
    initial state S {
      entry / { a1 = 0; }
      entry / { a2 = 0; }
    }
  }
}
