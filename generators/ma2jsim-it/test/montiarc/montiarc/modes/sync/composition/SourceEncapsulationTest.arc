/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test, ticks=[1,2,3,4,5,6], expected=[
  Untimed<OnOff.OFF>,
  Untimed<OnOff.OFF, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.ON, OnOff.OFF>,
  Untimed<OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF>,
  Untimed<OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON, OnOff.OFF, OnOff.ON>
]>>
component SourceEncapsulationTest(UntimedStream<OnOff> expected) {
  SourceEncapsulation sut;

  genI.out -> sut.i;
  sut.o -> assertions.actual;

  EmitList<OnOff> genI(Untimed<OnOff><>);

  AssertEqualsUntimed<OnOff> assertions(expected);
}
