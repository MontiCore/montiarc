/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[0,1,2,0,1,1,3], input=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON>,
  <OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.ON, OnOff.OFF>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.OFF>
], output=[
  Untimed<OnOff><>,
  Untimed<OnOff><>,
  Untimed<OnOff><>,
  Untimed<OnOff.OFF>,
  Untimed<OnOff.OFF>,
  Untimed<OnOff.OFF, OnOff.ON, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.OFF, OnOff.ON>
]>>
component SimpleTransitionOnEveryMessageTest(EventStream<OnOff> input, UntimedStream<OnOff> output) {
  SimpleTransitionOnEveryMessage sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions(output);
}
