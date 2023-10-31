/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.sync.automata.Inverter;
import montiarc.sync.automata.Medium;

component ParallelComposition {

  port in OnOff i1, i2;
  port out OnOff o1, o2;

  Medium sub1;
  Inverter sub2;

  i1 -> sub1.i;
  i2 -> sub2.i;

  sub1.o -> o1;
  sub2.o -> o2;
}
