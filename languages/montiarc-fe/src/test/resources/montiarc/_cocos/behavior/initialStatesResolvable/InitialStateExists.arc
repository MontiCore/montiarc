/* (c) https://github.com/MontiCore/monticore */
package behavior.initialStatesResolvable;

// Valid.
component InitialStateExists {
  port out int number;

  automaton {
    state Alpha;
    state Beta;
    initial Alpha / { number = 0; };

    Alpha -> Beta;
  }
}