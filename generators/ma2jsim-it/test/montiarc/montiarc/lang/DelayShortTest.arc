/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;
import java.lang.Short;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  [[Short.MIN_VALUE]],
  [[Short.MAX_VALUE]],
  [[Short.MIN_VALUE, Short.MIN_VALUE]],
  [[Short.MIN_VALUE, Short.MAX_VALUE]],
  [[Short.MAX_VALUE, Short.MIN_VALUE]],
  [[Short.MAX_VALUE, Short.MAX_VALUE]],
  [[Short.MIN_VALUE], [Short.MIN_VALUE]],
  [[Short.MIN_VALUE], [Short.MAX_VALUE]],
  [[Short.MAX_VALUE], [Short.MIN_VALUE]],
  [[Short.MAX_VALUE], [Short.MAX_VALUE]]
], output=[
  [[], [Short.MIN_VALUE]],
  [[], [Short.MAX_VALUE]],
  [[], [Short.MIN_VALUE, Short.MIN_VALUE]],
  [[], [Short.MIN_VALUE, Short.MAX_VALUE]],
  [[], [Short.MAX_VALUE, Short.MIN_VALUE]],
  [[], [Short.MAX_VALUE, Short.MAX_VALUE]],
  [[], [Short.MIN_VALUE], [Short.MIN_VALUE]],
  [[], [Short.MIN_VALUE], [Short.MAX_VALUE]],
  [[], [Short.MAX_VALUE], [Short.MIN_VALUE]],
  [[], [Short.MAX_VALUE], [Short.MAX_VALUE]]
]>>
component DelayShortTest(List<List<short>> input, List<List<short>> output) {
  DelayShort sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<short> generator(input);

  AssertEqualsTimed<short> assertions(output);
}
