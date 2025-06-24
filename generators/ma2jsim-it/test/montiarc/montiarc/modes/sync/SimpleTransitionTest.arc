/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=[0,1], input=[
  [],
  [OnOff.OFF]
], output=[
  [],
  [[OnOff.OFF]]
]>>
component SimpleTransitionTest(List<OnOff> input, List<List<OnOff>> output) {
  SimpleTransition sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
