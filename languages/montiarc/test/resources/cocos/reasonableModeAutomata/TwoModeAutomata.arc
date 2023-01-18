/* (c) https://github.com/MontiCore/monticore */
package reasonableModeAutomata;

/**
 * Invalid model that was derived from "CorrectModeAutomaton"
 */
component TwoModeAutomata {

  port in double reading;
  port out double readout;

  component Inverter i{
    port in double negative;
    port out double positive;
  }

  mode Forwarding {
    reading -> readout;
  }

  mode Correcting {
    i.positive -> readout;
  }

  mode Forwarding, Correcting {
    reading -> i.negative;
  }

  // using two automata is confusing and therefore forbidden
  mode automaton {
    initial Forwarding;
    Forwarding -> Correcting [reading < 0];
  }

  mode automaton {
    initial Correcting;
    Correcting -> Forwarding [reading > 0];
  }
}
