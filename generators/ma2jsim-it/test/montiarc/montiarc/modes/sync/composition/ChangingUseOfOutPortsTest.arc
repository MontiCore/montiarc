/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=[0,1,3], input=[
  [],
  [OnOff.ON],
  [OnOff.ON, OnOff.OFF, OnOff.ON]
], output=[
  [],
  [[OnOff.ON]],
  [[OnOff.ON], [OnOff.OFF], []]
]>>
component ChangingUseOfOutPortsTest(List<OnOff> input, List<List<OnOff>> output) {
  ChangingUseOfOutPorts sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
