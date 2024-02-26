/* (c) https://github.com/MontiCore/monticore */
package avionics.flight.guidance;

import avionics.flight.guidance.Signals.CMD;
import avionics.flight.guidance.Signals.SatelliteSignal;

component FlightSystem {

  feature advanced, dualGPS;

  port <<sync>> in SatelliteSignal sat;
  port <<sync>> in CMD pi;
  port <<sync>> out boolean of;

  PowerSupply powersupply;
  FlightControl flightControl;
  AutoFlightGuidance autoFlightGuidance;

  GPS gps1;
  sat -> gps1.sat;

  varif (dualGPS) {
    GPS gps2;
    GPSVoter voter;
    constraint(gps2.advanced == advanced);
  }

  pi -> autoFlightGuidance.cmd;
  powersupply.pow -> autoFlightGuidance.pow;
  powersupply.pow -> flightControl.pow;
  autoFlightGuidance.gc -> flightControl.gc;
  flightControl.fsc -> of;

  varif (dualGPS) {
    sat -> gps2.sat;
    gps1.pos -> voter.ip1;
    gps2.pos -> voter.ip2;
    voter.op -> autoFlightGuidance.pos;
  } else {
    gps1.pos -> autoFlightGuidance.pos;
  }

  constraint(autoFlightGuidance.advanced == advanced && gps1.advanced == advanced);
}