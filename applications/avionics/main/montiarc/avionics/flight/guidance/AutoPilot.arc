/* (c) https://github.com/MontiCore/monticore */
package avionics.flight.guidance;

import avionics.flight.guidance.Signals.CMD;
import avionics.flight.guidance.Signals.FlightData;

component AutoPilot {

  port <<sync>> in FlightData data;
  port <<sync>> in CMD op;
  port <<sync>> out CMD cmd;

}