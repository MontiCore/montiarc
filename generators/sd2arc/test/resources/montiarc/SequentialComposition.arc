/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.types.OnOff;
import montiarc.timed.Inverter;
import montiarc.timed.Medium;

component SequentialComposition {

  port in OnOff i;
  port out OnOff o;

  Medium sub1;
  Inverter sub2;
  Medium sub3;

  i -> sub1.i;

  sub1.o -> sub2.i;

  sub2.o -> sub3.i;

  sub3.o -> o;
}
