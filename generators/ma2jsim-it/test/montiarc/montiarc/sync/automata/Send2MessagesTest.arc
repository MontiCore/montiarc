/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,1,1,1,2], input=[
  Sync<OnOff.ON>,
  Sync<OnOff.OFF>,
  Sync<OnOff.ON>,
  Sync<OnOff.OFF>,
  Sync<OnOff.ON, OnOff.OFF>
], expected=[
  Untimed<OnOff.ON, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.OFF>,
  Untimed<OnOff.ON, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.OFF>,
  Untimed<OnOff.ON, OnOff.ON, OnOff.OFF, OnOff.OFF>
]>>
component Send2MessagesTest(SyncStream<OnOff> input, UntimedStream<OnOff> expected) {
  Send2Messages sut();

  generator.out -> sut.p;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions(expected);
}
