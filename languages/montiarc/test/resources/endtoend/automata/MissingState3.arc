/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The source state 'M1' and target state 'M2' of the transition
 * are missing (the state symbols cannot be resolved).
 */
component MissingState3 {

  automaton {
    initial state S;
    M1 -> M2;
  }
}
