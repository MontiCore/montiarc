/* (c) https://github.com/MontiCore/monticore */
package montiarc.specification.ucd;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=1, input=[<OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>], expected=[<OnOff><Tick, Tick, Tick>]>>
component UCDTest(EventStream<OnOff> input, EventStream<OnOff> expected) {
  UCD sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
