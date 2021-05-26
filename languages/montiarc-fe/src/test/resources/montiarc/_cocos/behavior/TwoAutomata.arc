/* (c) https://github.com/MontiCore/monticore */
package behavior;

// invalid, a component may only have one automata at max
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