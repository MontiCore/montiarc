/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;

<<test, ticks=[1,2,3], input=[
  Untimed<OnOff.ON>,
  Untimed<OnOff.ON, OnOff.ON>,
  Untimed<OnOff.ON, OnOff.ON, OnOff.ON>
]>>
component SourceEncapsulationTest(UntimedStream<OnOff> input) {
  SourceEncapsulation sut();

  sut.o -> assertions.actual;

  AssertEqualsUntimed<OnOff> assertions(input);
}
