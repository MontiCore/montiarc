/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,1, 2,2,2,2, 3,3,3,3,3,3,3,3], input=[
  Sync<OnOff.ON>,
  Sync<OnOff.OFF>,
  Sync<OnOff.ON, OnOff.ON>,
  Sync<OnOff.ON, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.OFF>,
  Sync<OnOff.ON, OnOff.ON, OnOff.ON>,
  Sync<OnOff.ON, OnOff.ON, OnOff.OFF>,
  Sync<OnOff.ON, OnOff.OFF, OnOff.ON>,
  Sync<OnOff.ON, OnOff.OFF, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.ON, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.ON, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.OFF, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.OFF, OnOff.OFF>
], output=[
   <OnOff.ON, Tick>,
   <OnOff.OFF, Tick>,
   <OnOff.ON, Tick, OnOff.ON, Tick>,
   <OnOff.ON, Tick, OnOff.OFF, Tick>,
   <OnOff.OFF, Tick, OnOff.ON, Tick>,
   <OnOff.OFF, Tick, OnOff.OFF, Tick>,
   <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>,
   <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.OFF, Tick>,
   <OnOff.ON, Tick, OnOff.OFF, Tick, OnOff.ON, Tick>,
   <OnOff.ON, Tick, OnOff.OFF, Tick, OnOff.OFF, Tick>,
   <OnOff.OFF, Tick, OnOff.ON, Tick, OnOff.ON, Tick>,
   <OnOff.OFF, Tick, OnOff.ON, Tick, OnOff.OFF, Tick>,
   <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.ON, Tick>,
   <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF, Tick>
 ]>>
component EncapsulationSyncInTest(SyncStream<OnOff> input, EventStream<OnOff> output) {
  EncapsulationSyncIn sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
