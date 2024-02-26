/* (c) https://github.com/MontiCore/monticore */
package avionics.flight.guidance;

import avionics.flight.guidance.Signals.SatelliteSignal;
import avionics.flight.guidance.Signals.Coordinates;

component GPS {

  feature advanced;

  port <<sync>> in SatelliteSignal sat;
  port <<sync>> out Coordinates pos;

}
