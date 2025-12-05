/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbol 'g' referenced in the guard of the internal
 * transition is missing (the symbol cannot be resolved).
 */
component MissingSymbolsInGuard3 {

  automaton {
    initial state S {
      -> [g];
    }
  }
}
