/* (c) https://github.com/MontiCore/monticore */
package montiarc.untimed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=[1,1,2,2,2,2,3], input=[
  [OnOff.ON],
  [OnOff.OFF],
  [OnOff.ON, OnOff.ON],
  [OnOff.ON, OnOff.OFF],
  [OnOff.OFF, OnOff.ON],
  [OnOff.OFF, OnOff.OFF],
  [OnOff.ON, OnOff.ON, OnOff.ON]
]>>
component MediumTest(List<OnOff> input) {
  Medium sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions(input);
}
