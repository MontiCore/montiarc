/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg event 'msg' of the inner transition is missing
 * (the event symbol cannot be resolved).
 */
component MissingEventInTransition4 {

  automaton {
    initial state S {
      -> msg;
    }
  }
}
