/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.sync.automata.Inverter;
import montiarc.sync.automata.Medium;

component SequentialComposition {

  port in OnOff i;
  port out OnOff o;

  Medium sub1;
  Inverter sub2;

  i -> sub1.i;

  sub1.o -> sub2.i;

  sub2.o -> o;
}
