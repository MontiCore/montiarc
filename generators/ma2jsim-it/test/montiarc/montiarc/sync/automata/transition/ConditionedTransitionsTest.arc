/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.types.NumberSign;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test={
// input                 output
  [[1, -3, 0],  [NumberSign.POSITIVE, NumberSign.NEGATIVE, NumberSign.ZERO]],
  [[-5, -3, -1],  [NumberSign.NEGATIVE, NumberSign.NEGATIVE, NumberSign.NEGATIVE]],
  [[100, 3, 1],  [NumberSign.POSITIVE, NumberSign.POSITIVE, NumberSign.POSITIVE]],
  [[0, 0, 0],  [NumberSign.ZERO, NumberSign.ZERO, NumberSign.ZERO]]
}, ticks=3>>
component ConditionedTransitionsTest(List<int> input, List<NumberSign> output) {
  ConditionedTransitions sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<Integer> generator(input);

  AssertEqualsUntimed<NumberSign> assertions(output);
}
