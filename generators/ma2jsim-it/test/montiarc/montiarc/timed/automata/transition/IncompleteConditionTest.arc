/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=[2,2,2,4],
input=[
  [[], []],
  [[1, 2], [3]],
  [[-1], [-3, -4]],
  [[1], [-3,2], [-1, -2], [5]]
], expected=[
  [[], []],
  [[OnOff.ON, OnOff.ON], [OnOff.ON]],
  [[], []],
  [[OnOff.ON], [OnOff.ON], [], [OnOff.ON]]
]>>
component IncompleteConditionTest(List<List<int>> input, List<List<OnOff>> expected) {
  IncompleteCondition sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
