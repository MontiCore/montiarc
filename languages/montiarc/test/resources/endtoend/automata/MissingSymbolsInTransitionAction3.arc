/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'a1' and 'a2' referenced in the actions of the
 * two transitions are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInTransitionAction3 {

  automaton {
    initial state S;
    S -> S / { a1 = 0; }
    S -> S / { a2 = 0; }
  }
}
