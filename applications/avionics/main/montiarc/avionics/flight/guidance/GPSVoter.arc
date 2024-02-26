/* (c) https://github.com/MontiCore/monticore */
package avionics.flight.guidance;

import avionics.flight.guidance.Signals.Coordinates;

component GPSVoter {

  port <<sync>> in Coordinates ip1;
  port <<sync>> in Coordinates ip2;
  port <<sync>> out Coordinates op;

}