/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg event 'msg' of the nested transition is missing
 * (the event symbol cannot be resolved).
 */
component MissingEventInTransition3 {

  automaton {
    initial state S {
      S -> S msg;
    }
  }
}
