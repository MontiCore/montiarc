/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import java.util.List;

<<test, ticks=[1,2,3], input=[
  [OnOff.ON],
  [OnOff.ON, OnOff.ON],
  [OnOff.ON, OnOff.ON, OnOff.ON]
]>>
component SourceEncapsulationTest(List<OnOff> input) {
  SourceEncapsulation sut();

  sut.o -> assertions.actual;

  AssertEqualsUntimed<OnOff> assertions(input);
}
