/* (c) https://github.com/MontiCore/monticore */
package reasonableModeAutomata;

/**
 * Invalid model that was derived from "CorrectModeAutomaton"
 */
component ModesInNonComposedComponent {

  port in double reading1;
  port in double reading2;
  port out double readout;

  // this component is atomic and therefore not allowed to have modes and a mode-automaton
  mode Forwarding {
    reading1 -> readout;
  }

  mode Correcting {
    reading2 -> readout;
  }

  mode automaton {
    initial Forwarding;
    Forwarding -> Correcting [reading1 < 0];
    Correcting -> Forwarding [reading2 < 0];
  }
}