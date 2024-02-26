/* (c) https://github.com/MontiCore/monticore */
package avionics.flight.guidance;

import avionics.flight.guidance.Signals.CMD;
import avionics.flight.guidance.Signals.Coordinates;
import avionics.flight.guidance.Signals.Power;

component AutoFlightGuidance {

  feature advanced;

  port <<sync>> in Coordinates pos;
  port <<sync>> in CMD cmd;
  port <<sync>> in Power pow;

  port <<sync>> out CMD gc;

  varif (advanced) {
    FlightGuidance flightGuidance;
    AutoPilot autoPilot;
    pos -> flightGuidance.i;
    cmd -> autoPilot.op;
    flightGuidance.fd -> autoPilot.data;
    autoPilot.cmd -> gc;
  } else {
    <<sync>> automaton {
      initial state Operational;
      state NonCriticalModeFailure;
      state CriticalModeFailure;

      Operational -> Operational / {gc = cmd.calc(pos);};
      Operational -> NonCriticalModeFailure [pos.lowPrec] / {gc = cmd.calc(pos);};
      Operational -> CriticalModeFailure [pow == Power.OFF] / {gc = CMD.NoService;};

      NonCriticalModeFailure -> NonCriticalModeFailure / {gc = cmd.calc(pos);};
      NonCriticalModeFailure -> Operational [!pos.lowPrec] / {gc = cmd.calc(pos);};
      NonCriticalModeFailure -> CriticalModeFailure [pow == Power.OFF] / {gc = CMD.NONE;};

      CriticalModeFailure -> CriticalModeFailure / {gc = CMD.NONE;};
    }
  }
}
