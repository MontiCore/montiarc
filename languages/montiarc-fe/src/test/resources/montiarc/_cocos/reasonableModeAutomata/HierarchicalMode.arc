/* (c) https://github.com/MontiCore/monticore */
package reasonableModeAutomata;

/**
 * Invalid model that was derived from "CorrectModeAutomaton"
 */
component HierarchicalMode {

  port in double reading;
  port out double readout;

  component Inverter i{
    port in double negative;
    port out double positive;
  }

  // this mode-construct may be kind of reasonable, but too ambitious and currently forbidden
  mode Forwarding {
    mode DontFeedInverter, FeedInverterAnyways{
      reading -> readout;
    }

    mode FeedInverterAnyways {
      reading -> i.negative;
    }

    mode automaton {
      initial DontFeedInverter;
      DontFeedInverter -> FeedInverterAnyways [reading < 0.5];
    }
  }

  mode Correcting {
    i.positive -> readout;
    reading -> i.negative;
  }

  mode automaton {
    initial Forwarding;
    Forwarding -> Correcting [reading < 0];
    Correcting -> Forwarding [reading > 0];
  }
}