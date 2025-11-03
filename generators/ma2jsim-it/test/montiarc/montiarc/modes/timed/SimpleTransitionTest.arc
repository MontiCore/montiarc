/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[0,1,2,0,1,1,2], input=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON>,
  <OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.ON, OnOff.OFF>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.OFF>
], output=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON>,
  <OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.OFF, OnOff.ON>,
  <OnOff.ON, Tick, OnOff.OFF, Tick, OnOff.ON>
]>>
component SimpleTransitionTest(EventStream<OnOff> input, EventStream<OnOff> output) {
  SimpleTransition sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(output);
}
