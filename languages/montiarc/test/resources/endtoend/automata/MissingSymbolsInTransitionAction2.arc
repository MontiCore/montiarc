/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbol 'a' referenced in the action of the transition is
 * missing (the symbol cannot be resolved).
 */
component MissingSymbolsInTransitionAction2 {

  automaton {
    initial state S;
    S -> S / { a = 0; }
  }
}
