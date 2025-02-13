/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.automata;

import montiarc.types.OnOff;

component WithMediumSub {

  port in OnOff inA, inB;
  port sync  in OnOff inY, inZ;

  port out OnOff outA, outB, outY, outZ;

  Medium medium;

  inA -> medium.inA;
  inB -> medium.inB;
  inY -> medium.inY;
  inZ -> medium.inZ;
  medium.outA -> outA;
  medium.outB -> outB;
  medium.outY -> outY;
  medium.outZ -> outZ;
}
