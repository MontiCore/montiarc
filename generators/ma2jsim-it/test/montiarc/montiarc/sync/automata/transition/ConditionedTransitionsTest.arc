/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.types.NumberSign;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  // input                 output
  [Sync<1, -3, 0>,   Untimed<NumberSign.POSITIVE, NumberSign.NEGATIVE, NumberSign.ZERO>],
  [Sync<-5, -3, -1>, Untimed<NumberSign.NEGATIVE, NumberSign.NEGATIVE, NumberSign.NEGATIVE>],
  [Sync<100, 3, 1>,  Untimed<NumberSign.POSITIVE, NumberSign.POSITIVE, NumberSign.POSITIVE>],
  [Sync<0, 0, 0>,    Untimed<NumberSign.ZERO, NumberSign.ZERO, NumberSign.ZERO>]
}, ticks=3>>
component ConditionedTransitionsTest(SyncStream<int> input, UntimedStream<NumberSign> output) {
  ConditionedTransitions sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<Integer> generator(input);

  AssertEqualsUntimed<NumberSign> assertions(output);
}
