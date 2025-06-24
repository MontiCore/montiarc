/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;
import java.lang.Float;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  [[Float.MIN_VALUE]],
  [[Float.MAX_VALUE]],
  [[Float.MIN_VALUE, Float.MIN_VALUE]],
  [[Float.MIN_VALUE, Float.MAX_VALUE]],
  [[Float.MAX_VALUE, Float.MIN_VALUE]],
  [[Float.MAX_VALUE, Float.MAX_VALUE]],
  [[Float.MIN_VALUE], [Float.MIN_VALUE]],
  [[Float.MIN_VALUE], [Float.MAX_VALUE]],
  [[Float.MAX_VALUE], [Float.MIN_VALUE]],
  [[Float.MAX_VALUE], [Float.MAX_VALUE]]
], output=[
  [[], [Float.MIN_VALUE]],
  [[], [Float.MAX_VALUE]],
  [[], [Float.MIN_VALUE, Float.MIN_VALUE]],
  [[], [Float.MIN_VALUE, Float.MAX_VALUE]],
  [[], [Float.MAX_VALUE, Float.MIN_VALUE]],
  [[], [Float.MAX_VALUE, Float.MAX_VALUE]],
  [[], [Float.MIN_VALUE], [Float.MIN_VALUE]],
  [[], [Float.MIN_VALUE], [Float.MAX_VALUE]],
  [[], [Float.MAX_VALUE], [Float.MIN_VALUE]],
  [[], [Float.MAX_VALUE], [Float.MAX_VALUE]]
]>>
component DelayFloatTest(List<List<float>> input, List<List<float>> output) {
  DelayFloat sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<float> generator(input);

  AssertEqualsTimed<float> assertions(output);
}
