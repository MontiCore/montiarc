/* (c) https://github.com/MontiCore/monticore */
package avionics.flight.guidance;

import avionics.flight.guidance.Signals.CMD;
import avionics.flight.guidance.Signals.Power;

component FlightControl {

  port <<sync>> in CMD gc;
  port <<sync>> in Power pow;
  port <<sync>> out boolean fsc;

}