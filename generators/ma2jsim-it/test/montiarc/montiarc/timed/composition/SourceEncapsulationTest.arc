/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=0, expected=<OnOff.ON>>>
component SourceEncapsulationTest(EventStream<OnOff> expected) {
  SourceEncapsulation sut();

  sut.o -> assertions.actual;

  AssertEqualsTimed<OnOff> assertions(expected);
}
