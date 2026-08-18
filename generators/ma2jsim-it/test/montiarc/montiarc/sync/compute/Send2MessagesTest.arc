/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<OnOff><>, Untimed<OnOff><>],
  [Sync<OnOff.ON>, Untimed<OnOff.ON, OnOff.ON>],
  [Sync<OnOff.ON, OnOff.ON>, Untimed<OnOff.ON, OnOff.ON, OnOff.ON, OnOff.ON>]
}, ticks=[1, 1, 2]>>
component Send2MessagesTest(SyncStream<OnOff> input, UntimedStream<OnOff> expected) {
  Send2Messages sut();

  generator.out -> sut.p;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions(expected);
}
