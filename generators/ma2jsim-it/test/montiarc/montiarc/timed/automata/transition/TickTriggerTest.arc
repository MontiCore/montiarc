/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[3,5],
input=[
  <1, 2, Tick, -2, Tick, 0, -1, Tick>,
  <1, 2, Tick, Tick, -2, Tick, Tick, Tick>
], expected=[
  <OnOff.OFF, OnOff.OFF, OnOff.ON, Tick, OnOff.OFF, OnOff.ON, Tick, OnOff.OFF, OnOff.OFF, OnOff.ON, Tick>,
  <OnOff.OFF, OnOff.OFF, OnOff.ON, Tick, OnOff.ON, Tick, OnOff.OFF, OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>
]>>
component TickTriggerTest(EventStream<int> input, EventStream<OnOff> expected) {
  TickTrigger sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
