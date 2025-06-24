/* (c) https://github.com/MontiCore/monticore */
package montiarc.untimed;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=[0,1,2,0,1,2], input=[
  [],
  [],
  [],
  [[OnOff.ON]],
  [[OnOff.ON],  [OnOff.ON]],
  [[OnOff.ON],  [OnOff.ON],  [OnOff.ON]]
], expected=[
  [],
  [[]],
  [[], []],
  [],
  [[]],
  [[], []]
]>>
component AtomicComponentTest(List<List<OnOff>> input, List<List<OnOff>> expected) {
  AtomicComponent sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
