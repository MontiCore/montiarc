/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=[1,1,2,2,2,2,3], input=[
  [OnOff.OFF],
  [OnOff.ON],
  [OnOff.ON, OnOff.ON],
  [OnOff.OFF, OnOff.OFF],
  [OnOff.ON, OnOff.OFF],
  [OnOff.OFF, OnOff.ON],
  [OnOff.ON, OnOff.ON, OnOff.ON]
], output=[
  [[]],
  [[]],
  [[], []],
  [[], []],
  [[], []],
  [[], []],
  [[], [], []]
]>>
component NoBehaviorTest(List<OnOff> input, List<List<OnOff>> output) {
  NoBehavior sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
