/* (c) https://github.com/MontiCore/monticore */
package reasonableModeAutomata;

/**
 * Invalid model that was derived from "CorrectModeAutomaton"
 */
component InitialModeMissing {

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
    initial Idle; // mode idle is not defined anywhere

    Idle -> Forwarding [reading >= 0];
    Idle -> Correcting [reading < 0];
    Forwarding -> Correcting [reading < 0];
    Correcting -> Forwarding [reading > 0];
  }
}