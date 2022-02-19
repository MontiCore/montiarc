/* (c) https://github.com/MontiCore/monticore */
package behavior.initialStatesResolvable;

// Invalid, as the initial output declaration does not refer to an existing state.
component InitialStateUnresolvable {
  port out int number;

  automaton {
    state Alpha;
    Alpha -> Alpha;

    initial Beta / { number = 0; };
  }
}