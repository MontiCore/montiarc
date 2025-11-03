/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[0,1,2,0,1,2], input=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON>,
  <OnOff.ON>,
  <OnOff.ON, Tick, OnOff.OFF, Tick, OnOff.ON, OnOff.OFF>
], output=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON>,
  <OnOff.ON, Tick>,
  <OnOff.ON, Tick, Tick, OnOff.ON>
]>>
component ChangingUseOfOutPortsTest(EventStream<OnOff> input, EventStream<OnOff> output) {
  ChangingUseOfOutPorts sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
