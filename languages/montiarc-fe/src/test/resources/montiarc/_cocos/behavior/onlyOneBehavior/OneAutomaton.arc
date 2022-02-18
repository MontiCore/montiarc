/* (c) https://github.com/MontiCore/monticore */
package behavior.onlyOneBehavior;

// Valid, one automaton is fine
component OneAutomaton {
  port in int number;

  automaton {
    state Alpha;
    state Beta;
    initial Alpha / {};

    Alpha -> Beta;
  }
}