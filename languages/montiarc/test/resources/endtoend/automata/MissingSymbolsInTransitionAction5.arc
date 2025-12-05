/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbol 'a' referenced in the action of the internal
 * transition is missing (the symbol cannot be resolved).
 */
component MissingSymbolsInTransitionAction5 {

  automaton {
    initial state S {
      -> / { a = 0; }
    }
  }
}
