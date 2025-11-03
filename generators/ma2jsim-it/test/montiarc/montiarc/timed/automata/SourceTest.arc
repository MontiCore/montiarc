/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;

<<test, ticks=[1,2,3], output=[
  Untimed<OnOff.ON>,
  Untimed<OnOff.ON, OnOff.ON>,
  Untimed<OnOff.ON, OnOff.ON, OnOff.ON>
]>>
component SourceTest(UntimedStream<OnOff> output) {
  Source sut();

  sut.o -> assertions.actual;

  AssertEqualsUntimed<OnOff> assertions(output);
}
