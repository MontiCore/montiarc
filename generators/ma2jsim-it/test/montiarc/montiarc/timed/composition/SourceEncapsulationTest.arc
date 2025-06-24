/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=0, expected=[[[OnOff.ON]]]>>
component SourceEncapsulationTest(List<List<OnOff>> expected) {
  SourceEncapsulation sut();

  sut.o -> assertions.actual;

  AssertEqualsTimed<OnOff> assertions(expected);
}
