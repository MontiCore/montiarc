/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.lang.Byte;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  <Byte.MIN_VALUE>,
  <Byte.MAX_VALUE>,
  <Byte.MIN_VALUE, Byte.MIN_VALUE>,
  <Byte.MIN_VALUE, Byte.MAX_VALUE>,
  <Byte.MAX_VALUE, Byte.MIN_VALUE>,
  <Byte.MAX_VALUE, Byte.MAX_VALUE>,
  <Byte.MIN_VALUE, Tick, Byte.MIN_VALUE>,
  <Byte.MIN_VALUE, Tick, Byte.MAX_VALUE>,
  <Byte.MAX_VALUE, Tick, Byte.MIN_VALUE>,
  <Byte.MAX_VALUE, Tick, Byte.MAX_VALUE>
], output=[
  <Tick, Byte.MIN_VALUE>,
  <Tick, Byte.MAX_VALUE>,
  <Tick, Byte.MIN_VALUE, Byte.MIN_VALUE>,
  <Tick, Byte.MIN_VALUE, Byte.MAX_VALUE>,
  <Tick, Byte.MAX_VALUE, Byte.MIN_VALUE>,
  <Tick, Byte.MAX_VALUE, Byte.MAX_VALUE>,
  <Tick, Byte.MIN_VALUE, Tick, Byte.MIN_VALUE>,
  <Tick, Byte.MIN_VALUE, Tick, Byte.MAX_VALUE>,
  <Tick, Byte.MAX_VALUE, Tick, Byte.MIN_VALUE>,
  <Tick, Byte.MAX_VALUE, Tick, Byte.MAX_VALUE>
]>>
component DelayByteTest(EventStream<byte> input, EventStream<byte> output) {
  DelayByte sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<byte> generator(input);

  AssertEqualsTimed<byte> assertions(output);
}
