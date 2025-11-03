/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[0,1,3], input=[
  Sync<OnOff><>,
  Sync<OnOff.ON>,
  Sync<OnOff.ON, OnOff.OFF, OnOff.ON>
], output=[
  <OnOff><>,
  <OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.OFF, Tick, Tick>
]>>
component ChangingUseOfOutPortsTest(SyncStream<OnOff> input, EventStream<OnOff> output) {
  ChangingUseOfOutPorts sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
