/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[1,1,1,1,1,1,1, 2,2,2,2,2,2,2,2,2,2,2, 3,3,3,3,3,3,3,3,3,3,3,3,3,3], input=[
  <OnOff.ON, Tick>,
  <OnOff.OFF, Tick>,
  <OnOff.ON, OnOff.ON, Tick>,
  <OnOff.ON, OnOff.OFF, Tick>,
  <OnOff.OFF, OnOff.ON, Tick>,
  <OnOff.OFF, OnOff.OFF, Tick>,
  <OnOff><Tick>,
  <Tick, OnOff.ON, Tick>,
  <Tick, OnOff.OFF, Tick>,
  <Tick, OnOff.ON, OnOff.ON, Tick>,
  <Tick, OnOff.ON, OnOff.OFF, Tick>,
  <Tick, OnOff.OFF, OnOff.ON, Tick>,
  <Tick, OnOff.OFF, OnOff.OFF, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.OFF, Tick>,
  <OnOff.OFF, Tick, OnOff.ON, Tick>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON, Tick, Tick, Tick>,
  <OnOff.OFF, Tick, Tick, Tick>,
  <Tick, OnOff.ON, Tick, Tick>,
  <Tick, OnOff.OFF, Tick, Tick>,
  <Tick, Tick, OnOff.ON, Tick>,
  <Tick, Tick, OnOff.OFF, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.OFF, Tick>,
  <OnOff.ON, Tick, OnOff.OFF, Tick, OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.OFF, Tick, OnOff.OFF, Tick>,
  <OnOff.OFF, Tick, OnOff.ON, Tick, OnOff.ON, Tick>,
  <OnOff.OFF, Tick, OnOff.ON, Tick, OnOff.OFF, Tick>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.ON, Tick>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF, Tick>
]>>
component EncapsulationTest(EventStream<OnOff> input) {
  Encapsulation sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(input);
}
