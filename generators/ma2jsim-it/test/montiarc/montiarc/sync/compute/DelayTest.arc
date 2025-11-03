/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=3, input=[
  Sync<OnOff.OFF, OnOff.OFF, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.OFF, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.ON,  OnOff.ON>,
  Sync<OnOff.ON,  OnOff.OFF, OnOff.ON>,
  Sync<OnOff.ON,  OnOff.ON,  OnOff.OFF>,
  Sync<OnOff.ON,  OnOff.ON,  OnOff.ON>
], expected=[
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.ON>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.ON,  Tick, OnOff.ON>,
  <OnOff.OFF, Tick, OnOff.ON,  Tick, OnOff.OFF, Tick, OnOff.ON>,
  <OnOff.OFF, Tick, OnOff.ON,  Tick, OnOff.ON,  Tick, OnOff.OFF>,
  <OnOff.OFF, Tick, OnOff.ON,  Tick, OnOff.ON,  Tick, OnOff.ON>
]>>
component DelayTest(SyncStream<OnOff> input, EventStream<OnOff> expected) {
  montiarc.sync.compute.Delay sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
