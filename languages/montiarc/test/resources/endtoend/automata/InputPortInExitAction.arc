/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Invalid model: An input port is referenced inside an exit action.
 */
component InputPortInExitAction {

  port in int i;

  automaton {
    initial state S {
      exit / { int x = i; }
    }
  }
}
