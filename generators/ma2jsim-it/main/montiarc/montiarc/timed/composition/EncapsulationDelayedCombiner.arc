/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.timed.automata.DelayedCombiner;
import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component EncapsulationDelayedCombiner {
  port in OnOff i1;
  port in OnOff i2;
  port out OnOff o;

  DelayedCombiner a;
  Medium b;

  i1 -> a.i1;
  i2 -> a.i2;

  a.o -> b.i;

  b.o -> o;
}
