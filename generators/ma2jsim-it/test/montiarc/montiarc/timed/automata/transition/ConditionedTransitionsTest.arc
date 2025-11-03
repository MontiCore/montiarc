/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.NumberSign;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=2,
input=[
  <1,2, Tick, -3,0, Tick, 0>
], expected=[
  <NumberSign.POSITIVE, NumberSign.POSITIVE, Tick, NumberSign.NEGATIVE, NumberSign.ZERO, Tick, NumberSign.ZERO>
]>>
component ConditionedTransitionsTest(EventStream<int> input, EventStream<NumberSign> expected) {
  ConditionedTransitions sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<NumberSign> assertions(expected);
}
