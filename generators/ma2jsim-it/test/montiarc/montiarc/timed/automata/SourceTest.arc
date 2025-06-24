/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import java.util.List;

<<test, ticks=[1,2,3], output=[
  [OnOff.ON],
  [OnOff.ON, OnOff.ON],
  [OnOff.ON, OnOff.ON, OnOff.ON]
]>>
component SourceTest(List<OnOff> output) {
  Source sut();

  sut.o -> assertions.actual;

  AssertEqualsUntimed<OnOff> assertions(output);
}
