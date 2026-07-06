/* (c) https://github.com/MontiCore/monticore */
package dahlquist;

import fmu.Dahlquist;

component DahlquistWrapper {
  port sync out double x;

  Dahlquist dahlquist(1.0);

  dahlquist.x -> x;
}
