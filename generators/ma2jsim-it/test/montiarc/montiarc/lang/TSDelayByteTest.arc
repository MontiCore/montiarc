/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Byte;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE],
input=[
  [Byte.MIN_VALUE],
  [Byte.MAX_VALUE],
  [Byte.MIN_VALUE],
  [Byte.MAX_VALUE],
  [Byte.MIN_VALUE, Byte.MIN_VALUE],
  [Byte.MIN_VALUE, Byte.MAX_VALUE],
  [Byte.MAX_VALUE, Byte.MIN_VALUE],
  [Byte.MAX_VALUE, Byte.MAX_VALUE],
  [Byte.MIN_VALUE, Byte.MIN_VALUE],
  [Byte.MIN_VALUE, Byte.MAX_VALUE],
  [Byte.MAX_VALUE, Byte.MIN_VALUE],
  [Byte.MAX_VALUE, Byte.MAX_VALUE]
], output=[
  [[Byte.MIN_VALUE]],
  [[Byte.MIN_VALUE]],
  [[Byte.MAX_VALUE]],
  [[Byte.MAX_VALUE]],
  [[Byte.MIN_VALUE], [Byte.MIN_VALUE]],
  [[Byte.MIN_VALUE], [Byte.MIN_VALUE]],
  [[Byte.MIN_VALUE], [Byte.MAX_VALUE]],
  [[Byte.MIN_VALUE], [Byte.MAX_VALUE]],
  [[Byte.MAX_VALUE], [Byte.MIN_VALUE]],
  [[Byte.MAX_VALUE], [Byte.MIN_VALUE]],
  [[Byte.MAX_VALUE], [Byte.MAX_VALUE]],
  [[Byte.MAX_VALUE], [Byte.MAX_VALUE]]
]>>
component TSDelayByteTest(byte init, List<byte> input, List<List<byte>> output) {
  TSDelayByte sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<byte> generator(input);

  AssertEqualsTimed<byte> assertions(output);
}
