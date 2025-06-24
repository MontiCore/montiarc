/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=[0,1,2,0,1,2], input=[
  [],
  [[]],
  [[], []],
  [[OnOff.ON]],
  [[OnOff.ON]],
  [[OnOff.ON], [OnOff.OFF], [OnOff.ON, OnOff.OFF]]
], output=[
  [],
  [[]],
  [[], []],
  [[OnOff.ON]],
  [[OnOff.ON]],
  [[OnOff.ON], [], [OnOff.ON]]
]>>
component ChangingUseOfOutPortsTest(List<List<OnOff>> input, List<List<OnOff>> output) {
  ChangingUseOfOutPorts sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
