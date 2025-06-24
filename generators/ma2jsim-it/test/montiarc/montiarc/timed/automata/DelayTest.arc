/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=3, input=[
  [[OnOff.OFF], [OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.ON],  [OnOff.ON]],
  [[OnOff.ON],  [OnOff.OFF], [OnOff.ON]],
  [[OnOff.ON],  [OnOff.ON],  [OnOff.OFF]],
  [[OnOff.ON],  [OnOff.ON],  [OnOff.ON]]
], expected=[
  [[OnOff.OFF], [OnOff.OFF], [OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.ON],  [OnOff.ON]],
  [[OnOff.OFF], [OnOff.ON],  [OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.ON],  [OnOff.ON],  [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.ON],  [OnOff.ON],  [OnOff.ON]]
]>>
component DelayTest(List<List<OnOff>> input, List<List<OnOff>> expected) {
  montiarc.timed.automata.Delay sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
