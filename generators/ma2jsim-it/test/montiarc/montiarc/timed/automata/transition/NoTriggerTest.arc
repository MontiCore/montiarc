/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[4,5],
input=[
  <1, 2, Tick, -2, Tick, 0, -1, Tick, Tick>,
  <1, 2, Tick, Tick, -2, Tick, Tick, Tick>
], expected=[
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>
]>>
component NoTriggerTest(EventStream<int> input, EventStream<OnOff> expected) {
  NoTrigger sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
