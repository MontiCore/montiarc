/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[2,2,2,4],
input=[
  <int><Tick>,
  <1, 2, Tick, 3>,
  <-1, Tick, -3, -4>,
  <1, Tick, -3,2, Tick, -1, -2, Tick, 5>
], expected=[
  <OnOff><Tick, Tick>,
  <OnOff.ON, OnOff.ON, Tick, OnOff.ON, Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick, Tick, OnOff.ON, Tick>
]>>
component IncompleteConditionTest(EventStream<int> input, EventStream<OnOff> expected) {
  IncompleteCondition sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
