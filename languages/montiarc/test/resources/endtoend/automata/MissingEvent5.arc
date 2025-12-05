/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg event 'msg' of an internal transition is missing
 * (the event symbol cannot be resolved).
 */
component MissingEvent5 {

  automaton {
    initial state S {
      -> msg;
    }
  }
}
