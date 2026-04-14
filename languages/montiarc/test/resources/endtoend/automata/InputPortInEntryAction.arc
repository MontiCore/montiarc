/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: An input port is referenced inside an entry action.
 */
component InputPortInEntryAction {

  port in int i;

  automaton {
    initial state S {
      entry / { int x = i; }
    }
  }
}
