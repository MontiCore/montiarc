/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,1,2,2,2,2,3], input=[
  Sync<OnOff.ON>,
  Sync<OnOff.OFF>,
  Sync<OnOff.ON, OnOff.ON>,
  Sync<OnOff.ON, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.OFF>,
  Sync<OnOff.ON, OnOff.ON, OnOff.ON>
]>>
component InnerComponentTest(SyncStream<OnOff> input) {
  InnerComponent sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions(input.untimed());
}
