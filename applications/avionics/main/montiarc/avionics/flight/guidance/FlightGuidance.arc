/* (c) https://github.com/MontiCore/monticore */
package avionics.flight.guidance;

import avionics.flight.guidance.Signals.Coordinates;
import avionics.flight.guidance.Signals.FlightData;

component FlightGuidance {

  port <<sync>> in Coordinates i;
  port <<sync>> out FlightData fd;

}