/* (c) https://github.com/MontiCore/monticore */
package behavior.onlyOneBehavior;

// Invalid, a component may only have one automata at max
component TwoAutomata {
  port in int number;

  automaton {
    state Alpha;
    state Beta;
    initial Alpha / {};

    Alpha -> Beta;
  }

  automaton {
    state Gamma;
    initial Gamma / {};

    Gamma -> Gamma;
  }
}