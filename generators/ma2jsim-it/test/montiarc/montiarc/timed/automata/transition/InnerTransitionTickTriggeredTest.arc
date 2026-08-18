/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[1,0,1], input=[
  <int><Tick>,
  <1>,
  <1, 2, Tick>
], expected=[
  <OnOff><Tick>,
  <OnOff.OFF>,
  <OnOff.OFF, OnOff.OFF, Tick>
]>>
component InnerTransitionTickTriggeredTest(EventStream<int> input, EventStream<OnOff> expected) {
  InnerTransitionTickTriggered sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
