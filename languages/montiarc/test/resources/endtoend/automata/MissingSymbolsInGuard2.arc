/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The symbols 'g1' and 'g2' in referenced in the guards of the
 * two transitions are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInGuard2 {

  automaton {
    initial state S;
    S -> S [g1];
    S -> S [g2];
  }
}
