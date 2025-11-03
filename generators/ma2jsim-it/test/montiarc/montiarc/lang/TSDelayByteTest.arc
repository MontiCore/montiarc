/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Byte;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE],
input=[
  Sync<Byte.MIN_VALUE>,
  Sync<Byte.MAX_VALUE>,
  Sync<Byte.MIN_VALUE>,
  Sync<Byte.MAX_VALUE>,
  Sync<Byte.MIN_VALUE, Byte.MIN_VALUE>,
  Sync<Byte.MIN_VALUE, Byte.MAX_VALUE>,
  Sync<Byte.MAX_VALUE, Byte.MIN_VALUE>,
  Sync<Byte.MAX_VALUE, Byte.MAX_VALUE>,
  Sync<Byte.MIN_VALUE, Byte.MIN_VALUE>,
  Sync<Byte.MIN_VALUE, Byte.MAX_VALUE>,
  Sync<Byte.MAX_VALUE, Byte.MIN_VALUE>,
  Sync<Byte.MAX_VALUE, Byte.MAX_VALUE>
], output=[
  <Byte.MIN_VALUE>,
  <Byte.MIN_VALUE>,
  <Byte.MAX_VALUE>,
  <Byte.MAX_VALUE>,
  <Byte.MIN_VALUE, Tick, Byte.MIN_VALUE>,
  <Byte.MIN_VALUE, Tick, Byte.MIN_VALUE>,
  <Byte.MIN_VALUE, Tick, Byte.MAX_VALUE>,
  <Byte.MIN_VALUE, Tick, Byte.MAX_VALUE>,
  <Byte.MAX_VALUE, Tick, Byte.MIN_VALUE>,
  <Byte.MAX_VALUE, Tick, Byte.MIN_VALUE>,
  <Byte.MAX_VALUE, Tick, Byte.MAX_VALUE>,
  <Byte.MAX_VALUE, Tick, Byte.MAX_VALUE>
]>>
component TSDelayByteTest(byte init, SyncStream<byte> input, EventStream<byte> output) {
  TSDelayByte sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<byte> generator(input);

  AssertEqualsTimed<byte> assertions(output);
}
