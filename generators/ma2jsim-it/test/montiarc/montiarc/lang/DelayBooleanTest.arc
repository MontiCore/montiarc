/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  <true>,
  <false>,
  <true, true>,
  <true, false>,
  <false, true>,
  <false, false>,
  <true, Tick, true>,
  <true, Tick, false>,
  <false, Tick, true>,
  <false, Tick, false>
], output=[
  <Tick, true>,
  <Tick, false>,
  <Tick, true, true>,
  <Tick, true, false>,
  <Tick, false, true>,
  <Tick, false, false>,
  <Tick, true, Tick, true>,
  <Tick, true, Tick, false>,
  <Tick, false, Tick, true>,
  <Tick, false, Tick, false>
]>>
component DelayBooleanTest(EventStream<boolean> input, EventStream<boolean> output) {
  DelayBoolean sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<boolean> generator(input);

  AssertEqualsTimed<boolean> assertions(output);
}
