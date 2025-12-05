/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbol 'a' referenced in the exit action of the state
 * is missing (the symbol cannot be resolved).
 */
component MissingSymbolsInExitAction1 {

  automaton {
    initial state S {
      exit / a = 0;
    }
  }
}
