/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.lang.Float;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  <Float.MIN_VALUE>,
  <Float.MAX_VALUE>,
  <Float.MIN_VALUE, Float.MIN_VALUE>,
  <Float.MIN_VALUE, Float.MAX_VALUE>,
  <Float.MAX_VALUE, Float.MIN_VALUE>,
  <Float.MAX_VALUE, Float.MAX_VALUE>,
  <Float.MIN_VALUE, Tick, Float.MIN_VALUE>,
  <Float.MIN_VALUE, Tick, Float.MAX_VALUE>,
  <Float.MAX_VALUE, Tick, Float.MIN_VALUE>,
  <Float.MAX_VALUE, Tick, Float.MAX_VALUE>
], output=[
  <Tick, Float.MIN_VALUE>,
  <Tick, Float.MAX_VALUE>,
  <Tick, Float.MIN_VALUE, Float.MIN_VALUE>,
  <Tick, Float.MIN_VALUE, Float.MAX_VALUE>,
  <Tick, Float.MAX_VALUE, Float.MIN_VALUE>,
  <Tick, Float.MAX_VALUE, Float.MAX_VALUE>,
  <Tick, Float.MIN_VALUE, Tick, Float.MIN_VALUE>,
  <Tick, Float.MIN_VALUE, Tick, Float.MAX_VALUE>,
  <Tick, Float.MAX_VALUE, Tick, Float.MIN_VALUE>,
  <Tick, Float.MAX_VALUE, Tick, Float.MAX_VALUE>
]>>
component DelayFloatTest(EventStream<float> input, EventStream<float> output) {
  DelayFloat sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<float> generator(input);

  AssertEqualsTimed<float> assertions(output);
}
