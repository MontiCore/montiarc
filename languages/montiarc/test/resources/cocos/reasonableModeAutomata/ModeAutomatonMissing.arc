/* (c) https://github.com/MontiCore/monticore */
package reasonableModeAutomata;

/**
 * Invalid model that was derived from "CorrectModeAutomaton"
 */
component ModeAutomatonMissing {

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

  // modes are useless without a corresponding automaton
}
