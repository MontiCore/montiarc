/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The target state 'M' of the transition is missing (the state
 * symbol cannot be resolved).
 */
component MissingStateInTransition2 {

  automaton {
    initial state S;
    S -> M;
  }
}
