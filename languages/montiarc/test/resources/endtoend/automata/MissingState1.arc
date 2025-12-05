/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The source state 'M' of the transition is missing (the state
 * symbol cannot be resolved).
 */
component MissingState1 {

  automaton {
    initial state S;
    M -> S;
  }
}
