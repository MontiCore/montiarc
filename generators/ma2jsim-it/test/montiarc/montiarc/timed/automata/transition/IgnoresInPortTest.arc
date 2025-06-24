/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=[1,1,2],
input1=[
  [[OnOff.ON, OnOff.ON], [OnOff.ON, OnOff.ON]],
  [[], [OnOff.ON]],
  [[OnOff.ON], [OnOff.ON], [OnOff.ON]]
], input2=[
  [[OnOff.OFF], []],
  [[OnOff.OFF, OnOff.OFF], []],
  [[OnOff.ON], [OnOff.ON], []]
], expected=[
  [[OnOff.ON, OnOff.ON], [OnOff.ON, OnOff.ON]],
  [[], [OnOff.ON]],
  [[OnOff.ON], [OnOff.ON], [OnOff.ON]]
]>>
component IgnoresInPortTest(List<List<OnOff>> input1, List<List<OnOff>> input2, List<List<OnOff>> expected) {
  IgnoresInPort sut();

  generatorI1.out -> sut.i1;
  generatorI2.out -> sut.i2;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generatorI1(input1);
  EmitTimed<OnOff> generatorI2(input2);

  AssertEqualsTimed<OnOff> assertions(expected);
}
