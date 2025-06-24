/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.Emit;
import java.util.List;

<<test, ticks=[0,1,2,3], expected=[[], [[]], [[],[]], [[],[],[]]]>>
component UnusedInPortsTest(List<List<Boolean>> expected) {
  UnusedInPorts sut();

  emitter.out -> sut.i1, sut.i2;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  Emit<Boolean> emitter(null);
  AssertEqualsTimed<boolean> assertions1(expected);
  AssertEqualsTimed<boolean> assertions2(expected);
}
