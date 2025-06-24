/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.NumberSign;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=2,
input=[
  [[1,2], [-3,0], [0]]
], expected=[
  [[NumberSign.POSITIVE, NumberSign.POSITIVE], [NumberSign.NEGATIVE, NumberSign.ZERO], [NumberSign.ZERO]]
]>>
component ConditionedTransitionsTest(List<List<int>> input, List<List<NumberSign>> expected) {
  ConditionedTransitions sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<NumberSign> assertions(expected);
}
