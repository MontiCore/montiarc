/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'a1' and 'a2' referenced in the two exit actions
 * of the state are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInExitAction3 {

  automaton {
    initial state S {
      exit / { a1 = 0; }
      exit / { a2 = 0; }
    }
  }
}
