/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
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
], expected=[
  [OnOff.ON],
  [OnOff.OFF],
  [OnOff.OFF, OnOff.OFF],
  [OnOff.ON, OnOff.ON],
  [OnOff.OFF, OnOff.ON],
  [OnOff.ON, OnOff.OFF],
  [OnOff.OFF, OnOff.OFF, OnOff.OFF]
]>>
component InverterTest(List<OnOff> input, List<OnOff> expected) {
  Inverter sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions(expected);
}
