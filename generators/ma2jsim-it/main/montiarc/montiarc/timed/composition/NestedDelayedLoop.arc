/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component NestedDelayedLoop {
  port in OnOff i;
  port out OnOff o;

  EncapsulationDelayedCombiner c;
  Medium d;

  i -> c.i1;

  d.o -> c.i2;

  c.o -> d.i;
  c.o -> o;
}
