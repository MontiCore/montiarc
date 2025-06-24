/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;
import java.lang.Integer;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  [[Integer.MIN_VALUE]],
  [[Integer.MAX_VALUE]],
  [[Integer.MIN_VALUE, Integer.MIN_VALUE]],
  [[Integer.MIN_VALUE, Integer.MAX_VALUE]],
  [[Integer.MAX_VALUE, Integer.MIN_VALUE]],
  [[Integer.MAX_VALUE, Integer.MAX_VALUE]],
  [[Integer.MIN_VALUE], [Integer.MIN_VALUE]],
  [[Integer.MIN_VALUE], [Integer.MAX_VALUE]],
  [[Integer.MAX_VALUE], [Integer.MIN_VALUE]],
  [[Integer.MAX_VALUE], [Integer.MAX_VALUE]]
], output=[
  [[], [Integer.MIN_VALUE]],
  [[], [Integer.MAX_VALUE]],
  [[], [Integer.MIN_VALUE, Integer.MIN_VALUE]],
  [[], [Integer.MIN_VALUE, Integer.MAX_VALUE]],
  [[], [Integer.MAX_VALUE, Integer.MIN_VALUE]],
  [[], [Integer.MAX_VALUE, Integer.MAX_VALUE]],
  [[], [Integer.MIN_VALUE], [Integer.MIN_VALUE]],
  [[], [Integer.MIN_VALUE], [Integer.MAX_VALUE]],
  [[], [Integer.MAX_VALUE], [Integer.MIN_VALUE]],
  [[], [Integer.MAX_VALUE], [Integer.MAX_VALUE]]
]>>
component DelayIntegerTest(List<List<int>> input, List<List<int>> output) {
  DelayInteger sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<int> generator(input);

  AssertEqualsTimed<int> assertions(output);
}
