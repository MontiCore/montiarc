/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbol 'g' referenced in the guard of the transition is
 * missing (the symbol cannot be resolved).
 */
component MissingSymbolsInGuard1 {

  automaton {
    initial state S;
    S -> S [g];
  }
}
