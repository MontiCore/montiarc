/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=1, input=[<OnOff><Tick>], expected=[<OnOff><Tick>]>>
component NestedDelayedLoopTest(EventStream<OnOff> input, EventStream<OnOff> expected) {
  NestedDelayedLoop sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
