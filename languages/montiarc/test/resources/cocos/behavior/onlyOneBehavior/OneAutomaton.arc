/* (c) https://github.com/MontiCore/monticore */
package behavior.onlyOneBehavior;

// Valid, one automaton is fine
component OneAutomaton {
  port in int number;

  automaton {
    initial {} state Alpha;
    state Beta;

    Alpha -> Beta;
  }
}
