/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg event 'msg' is missing (the event symbol cannot be resolved).
 */
component MissingEventInTransition1 {

  automaton {
    initial state S;
    S -> S msg;
  }
}
