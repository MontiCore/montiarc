/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The source state 'M1' and target state 'M2' of the transitions
 * are missing (the state symbols cannot be resolved).
 */
component MissingStateInTransition4 {

  automaton {
    initial state S;
    M1 -> S;
    S -> M2;
  }
}
