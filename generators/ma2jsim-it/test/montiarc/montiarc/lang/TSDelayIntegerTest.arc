/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Integer;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE],
input=[
  [Integer.MIN_VALUE],
  [Integer.MAX_VALUE],
  [Integer.MIN_VALUE],
  [Integer.MAX_VALUE],
  [Integer.MIN_VALUE, Integer.MIN_VALUE],
  [Integer.MIN_VALUE, Integer.MAX_VALUE],
  [Integer.MAX_VALUE, Integer.MIN_VALUE],
  [Integer.MAX_VALUE, Integer.MAX_VALUE],
  [Integer.MIN_VALUE, Integer.MIN_VALUE],
  [Integer.MIN_VALUE, Integer.MAX_VALUE],
  [Integer.MAX_VALUE, Integer.MIN_VALUE],
  [Integer.MAX_VALUE, Integer.MAX_VALUE]
], output=[
  [[Integer.MIN_VALUE]],
  [[Integer.MIN_VALUE]],
  [[Integer.MAX_VALUE]],
  [[Integer.MAX_VALUE]],
  [[Integer.MIN_VALUE], [Integer.MIN_VALUE]],
  [[Integer.MIN_VALUE], [Integer.MIN_VALUE]],
  [[Integer.MIN_VALUE], [Integer.MAX_VALUE]],
  [[Integer.MIN_VALUE], [Integer.MAX_VALUE]],
  [[Integer.MAX_VALUE], [Integer.MIN_VALUE]],
  [[Integer.MAX_VALUE], [Integer.MIN_VALUE]],
  [[Integer.MAX_VALUE], [Integer.MAX_VALUE]],
  [[Integer.MAX_VALUE], [Integer.MAX_VALUE]]
]>>
component TSDelayIntegerTest(int init, List<int> input, List<List<int>> output) {
  TSDelayInteger sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<int> generator(input);

  AssertEqualsTimed<int> assertions(output);
}
