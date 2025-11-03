/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,1,2,2,2,2,3,3], input=[
  Sync<OnOff.ON>,
  Sync<OnOff.OFF>,
  Sync<OnOff.ON, OnOff.ON>,
  Sync<OnOff.ON, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.OFF>,
  Sync<OnOff.ON, OnOff.ON, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.OFF, OnOff.OFF>
], expected=[
  Untimed<OnOff.OFF>,
  Untimed<OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.OFF>,
  Untimed<OnOff.OFF, OnOff.ON>,
  Untimed<OnOff.ON, OnOff.OFF>,
  Untimed<OnOff.ON, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.OFF, OnOff.OFF>,
  Untimed<OnOff.ON, OnOff.ON, OnOff.ON>
]>>
component InverterTest(SyncStream<OnOff> input, UntimedStream<OnOff> expected) {
  Inverter sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions(expected);
}
