/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=[3,5],
input=[
  [[1, 2], [-2], [0, -1]],
  [[1, 2], [], [-2], [], []]
], expected=[
  [[OnOff.OFF, OnOff.OFF, OnOff.ON], [OnOff.OFF, OnOff.ON], [OnOff.OFF, OnOff.OFF, OnOff.ON]],
  [[OnOff.OFF, OnOff.OFF, OnOff.ON], [OnOff.ON], [OnOff.OFF, OnOff.ON], [OnOff.ON], [OnOff.ON]]
]>>
component TickTriggerTest(List<List<int>> input, List<List<OnOff>> expected) {
  TickTrigger sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
