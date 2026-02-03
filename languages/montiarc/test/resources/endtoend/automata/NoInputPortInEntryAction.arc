/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: An input port is referenced in a variable declaration inside an entry action.
 */
component NoInputPortInEntryAction {

  port in int i;

  automaton {
    initial state S {
      entry / { int x = i; }
    }
  }
}
