/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: A non-synchronous input port is referenced inside a do action.
 */
component NonSyncInputPortInDoAction {
  port in int i;

  automaton {
    initial state S {
      do / {
        int x = i;
      }
    }
  }
}
