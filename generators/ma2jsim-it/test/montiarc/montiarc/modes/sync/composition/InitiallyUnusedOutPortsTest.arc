/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=[0,1,2], input=[
  [],
  [OnOff.ON],
  [OnOff.ON, OnOff.OFF]
], output=[
  [],
  [[OnOff.ON]],
  [[OnOff.ON], [OnOff.OFF]]
]>>
component InitiallyUnusedOutPortsTest(List<OnOff> input, List<List<OnOff>> output) {
  InitiallyUnusedOutPorts sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
