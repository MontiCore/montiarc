/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,1,1,2,2], input=[
  Sync<OnOff><>,
  Sync<OnOff.ON>,
  Sync<OnOff.ON>,
  Sync<OnOff><>,
  Sync<OnOff.ON, OnOff.ON>
], output=[
  <OnOff><Tick>,
  <OnOff><Tick>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><Tick, Tick>
]>>
component SendNoMessagesTest(SyncStream<OnOff> input, EventStream<OnOff> output) {
  SendNoMessages sut();

  generator.out -> sut.p;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
