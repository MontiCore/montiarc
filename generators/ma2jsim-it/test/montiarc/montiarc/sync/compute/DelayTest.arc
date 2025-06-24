/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=3, input=[
  [OnOff.OFF, OnOff.OFF, OnOff.OFF],
  [OnOff.OFF, OnOff.OFF, OnOff.ON],
  [OnOff.OFF, OnOff.ON,  OnOff.ON],
  [OnOff.ON,  OnOff.OFF, OnOff.ON],
  [OnOff.ON,  OnOff.ON,  OnOff.OFF],
  [OnOff.ON,  OnOff.ON,  OnOff.ON]
], expected=[
  [[OnOff.OFF], [OnOff.OFF], [OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.ON],  [OnOff.ON]],
  [[OnOff.OFF], [OnOff.ON],  [OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.ON],  [OnOff.ON],  [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.ON],  [OnOff.ON],  [OnOff.ON]]
]>>
component DelayTest(List<OnOff> input, List<List<OnOff>> expected) {
  montiarc.sync.compute.Delay sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
