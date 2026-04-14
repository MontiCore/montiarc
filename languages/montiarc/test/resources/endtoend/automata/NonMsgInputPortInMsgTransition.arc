/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: A message-triggered transition references an input port other than the message port.
 */
component NonMsgInputPortInMsgTransition {
  port in int i1, i2;

  automaton {
    initial state S;
    S -> S i1 / {
      int x = i2;
    }
  }
}
