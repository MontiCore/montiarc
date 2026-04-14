/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: A non-synchronous input port is referenced inside an epsilon transition action.
 */
component NonSyncInputPortInEpsilonTransition {
  port in int i;

  automaton {
    initial state S;
    S -> S / {
      int x = i;
    }
  }
}
