/* (c) https://github.com/MontiCore/monticore */
package reasonableModeAutomata;

/**
 * Valid model.
 * Base for all deviated models in this directory;
 */
component CorrectModeAutomaton {

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

  mode automaton {
    initial Forwarding;

    Forwarding -> Correcting [reading < 0];
    Correcting -> Forwarding [reading > 0];
  }
}
