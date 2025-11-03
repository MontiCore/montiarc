/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=2,
input=[
  <1, -2, Tick, Tick>,
  <Tick, -2, -1, Tick>,
  <Tick, 4, 5, Tick>
], expected=[
  <OnOff.ON, OnOff.OFF, Tick, OnOff.OFF, Tick>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick>,
  <OnOff.OFF, Tick, OnOff.ON, OnOff.ON, OnOff.OFF, Tick>
]>>
component NoTriggerVsMessageTransitionTest(EventStream<int> input, EventStream<OnOff> expected) {
  NoTriggerVsMessageTransition sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
