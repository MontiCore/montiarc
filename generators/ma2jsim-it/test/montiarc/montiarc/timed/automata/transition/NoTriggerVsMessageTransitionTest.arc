/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=2,
input=[
  [[1, -2], []],
  [[], [-2, -1]],
  [[], [4, 5]]
], expected=[
  [[OnOff.ON, OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.ON, OnOff.ON, OnOff.OFF]]
]>>
component NoTriggerVsMessageTransitionTest(List<List<int>> input, List<List<OnOff>> expected) {
  NoTriggerVsMessageTransition sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
