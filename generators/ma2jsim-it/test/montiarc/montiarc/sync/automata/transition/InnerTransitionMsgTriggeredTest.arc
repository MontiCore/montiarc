/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.types.OnOff;
import montiarc.types.OnOff.*;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  [Sync<int><>, Untimed<OnOff><OFF, OFF>],
  [Sync<1>,    Untimed<OnOff><OFF, OFF>],
  [Sync<1>,    Untimed<OnOff><OFF>],
  [Sync<1>,    Untimed<OnOff><>]
}, ticks=[2, 2, 1, 0]>>
component InnerTransitionMsgTriggeredTest(SyncStream<int> input, UntimedStream<OnOff> expected) {
  InnerTransitionMsgTriggered sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<int> generator(input);

  AssertEqualsUntimed<OnOff> assertions(expected);
}
