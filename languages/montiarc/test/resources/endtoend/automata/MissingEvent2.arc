/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The msg events 'msg1' and "msg2" are missing (the event
 * symbols cannot be resolved).
 */
component MissingEvent2 {

  automaton {
    initial state S;
    S -> S msg1;
    S -> S msg2;
  }
}
