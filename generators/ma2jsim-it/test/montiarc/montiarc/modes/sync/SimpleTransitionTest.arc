/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[0,1], input=[
  Sync<OnOff><>,
  Sync<OnOff.OFF>
], output=[
  <OnOff><>,
  <OnOff.OFF, Tick>
]>>
component SimpleTransitionTest(SyncStream<OnOff> input, EventStream<OnOff> output) {
  SimpleTransition sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
