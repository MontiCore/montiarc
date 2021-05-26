/* (c) https://github.com/MontiCore/monticore */
package behavior;

// valid, one automaton is fine
component OneAutomaton {
  port in int number;

  automaton {
    initial state Alpha;
    state Beta;

    Alpha -> Beta;
  }
}