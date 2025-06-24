/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;
import java.lang.Byte;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  [[Byte.MIN_VALUE]],
  [[Byte.MAX_VALUE]],
  [[Byte.MIN_VALUE, Byte.MIN_VALUE]],
  [[Byte.MIN_VALUE, Byte.MAX_VALUE]],
  [[Byte.MAX_VALUE, Byte.MIN_VALUE]],
  [[Byte.MAX_VALUE, Byte.MAX_VALUE]],
  [[Byte.MIN_VALUE], [Byte.MIN_VALUE]],
  [[Byte.MIN_VALUE], [Byte.MAX_VALUE]],
  [[Byte.MAX_VALUE], [Byte.MIN_VALUE]],
  [[Byte.MAX_VALUE], [Byte.MAX_VALUE]]
], output=[
  [[], [Byte.MIN_VALUE]],
  [[], [Byte.MAX_VALUE]],
  [[], [Byte.MIN_VALUE, Byte.MIN_VALUE]],
  [[], [Byte.MIN_VALUE, Byte.MAX_VALUE]],
  [[], [Byte.MAX_VALUE, Byte.MIN_VALUE]],
  [[], [Byte.MAX_VALUE, Byte.MAX_VALUE]],
  [[], [Byte.MIN_VALUE], [Byte.MIN_VALUE]],
  [[], [Byte.MIN_VALUE], [Byte.MAX_VALUE]],
  [[], [Byte.MAX_VALUE], [Byte.MIN_VALUE]],
  [[], [Byte.MAX_VALUE], [Byte.MAX_VALUE]]
]>>
component DelayByteTest(List<List<byte>> input, List<List<byte>> output) {
  DelayByte sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<byte> generator(input);

  AssertEqualsTimed<byte> assertions(output);
}
