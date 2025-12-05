/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbol 'a' referenced in the entry action of the state
 * is missing (the symbol cannot be resolved).
 */
component MissingSymbolsInEntryAction2 {

  automaton {
    initial state S {
      entry / { a = 0; }
    }
  }
}
