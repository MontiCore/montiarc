/* (c) https://github.com/MontiCore/monticore */
package modes;

/**
 * Invalid model: An inner component contains a mode automaton that contains a state declaration.
 */
component StateInModeAutomaton3 {

  component Inner {

    mode automaton {
      initial mode M1 { }
      state S1;
    }
  }
}
