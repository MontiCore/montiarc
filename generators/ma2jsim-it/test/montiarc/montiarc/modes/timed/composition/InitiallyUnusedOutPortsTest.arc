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
  <OnOff.ON, Tick, OnOff.OFF, OnOff.ON>
], output=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON>,
  <OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.OFF, OnOff.ON, Tick>
]>>
component InitiallyUnusedOutPortsTest(EventStream<OnOff> input, EventStream<OnOff> output) {
  InitiallyUnusedOutPorts sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
