/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.timed.automata.DelayedCombiner;
import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component FlatDelayedLoop {
  port in OnOff i;
  port out OnOff o;

  DelayedCombiner a;
  Medium b;
  Medium d;

  i -> a.i1;

  a.o -> b.i;
  b.o -> d.i;
  d.o -> a.i2;

  b.o -> o;
}
