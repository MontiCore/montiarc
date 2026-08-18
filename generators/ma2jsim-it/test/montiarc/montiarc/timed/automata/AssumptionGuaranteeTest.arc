/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=1, input=[<OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>], expected=[<OnOff><Tick, Tick, Tick>]>>
component AssumptionGuaranteeTest(EventStream<OnOff> input, EventStream<OnOff> expected) {
  AG sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
