/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,1,2,2,2,2,3], input=[
  Sync<OnOff.OFF>,
  Sync<OnOff.ON>,
  Sync<OnOff.ON, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.OFF>,
  Sync<OnOff.ON, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.ON>,
  Sync<OnOff.ON, OnOff.ON, OnOff.ON>
], output=[
  <OnOff><Tick>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><Tick, Tick, Tick>
]>>
component NoBehaviorTest(SyncStream<OnOff> input, EventStream<OnOff> output) {
  NoBehavior sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
