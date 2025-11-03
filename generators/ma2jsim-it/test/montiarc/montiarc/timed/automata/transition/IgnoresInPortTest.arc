/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[1,1,2],
input1=[
  <OnOff.ON, OnOff.ON, Tick, OnOff.ON, OnOff.ON>,
  <Tick, OnOff.ON>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON>
], input2=[
  <OnOff.OFF, Tick>,
  <OnOff.OFF, OnOff.OFF, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick>
], expected=[
  <OnOff.ON, OnOff.ON, Tick, OnOff.ON, OnOff.ON>,
  <Tick, OnOff.ON>,
  <OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON>
]>>
component IgnoresInPortTest(EventStream<OnOff> input1, EventStream<OnOff> input2, EventStream<OnOff> expected) {
  IgnoresInPort sut();

  generatorI1.out -> sut.i1;
  generatorI2.out -> sut.i2;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generatorI1(input1);
  EmitTimed<OnOff> generatorI2(input2);

  AssertEqualsTimed<OnOff> assertions(expected);
}
