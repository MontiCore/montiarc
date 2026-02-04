/* (c) https://github.com/MontiCore/monticore */
package automata;

/*
 * Invalid model: The inner component contains an automaton without initial state.
 */
component NoInitialState2 {
  component Inner {
    automaton { }
  }
}
