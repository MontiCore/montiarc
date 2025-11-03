/* (c) https://github.com/MontiCore/monticore */
package montiarc.untimed;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[0,1,2,0,1,2], input=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>
], expected=[
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>
]>>
component AtomicComponentTest(EventStream<OnOff> input, EventStream<OnOff> expected) {
  AtomicComponent sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
