/* (c) https://github.com/MontiCore/monticore */
package behavior.onlyOneBehavior;

// Invalid, a component may only have one automata at max
component TwoAutomata {
  port in int number;

  automaton {
    initial state Alpha;
    state Beta;

    Alpha -> Beta;
  }

  automaton {
    initial state Gamma;

    Gamma -> Gamma;
  }
}
