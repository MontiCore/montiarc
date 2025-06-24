/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Float;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE],
input=[
  [Float.MIN_VALUE],
  [Float.MAX_VALUE],
  [Float.MIN_VALUE],
  [Float.MAX_VALUE],
  [Float.MIN_VALUE, Float.MIN_VALUE],
  [Float.MIN_VALUE, Float.MAX_VALUE],
  [Float.MAX_VALUE, Float.MIN_VALUE],
  [Float.MAX_VALUE, Float.MAX_VALUE],
  [Float.MIN_VALUE, Float.MIN_VALUE],
  [Float.MIN_VALUE, Float.MAX_VALUE],
  [Float.MAX_VALUE, Float.MIN_VALUE],
  [Float.MAX_VALUE, Float.MAX_VALUE]
], output=[
  [[Float.MIN_VALUE]],
  [[Float.MIN_VALUE]],
  [[Float.MAX_VALUE]],
  [[Float.MAX_VALUE]],
  [[Float.MIN_VALUE], [Float.MIN_VALUE]],
  [[Float.MIN_VALUE], [Float.MIN_VALUE]],
  [[Float.MIN_VALUE], [Float.MAX_VALUE]],
  [[Float.MIN_VALUE], [Float.MAX_VALUE]],
  [[Float.MAX_VALUE], [Float.MIN_VALUE]],
  [[Float.MAX_VALUE], [Float.MIN_VALUE]],
  [[Float.MAX_VALUE], [Float.MAX_VALUE]],
  [[Float.MAX_VALUE], [Float.MAX_VALUE]]
]>>
component TSDelayFloatTest(float init, List<float> input, List<List<float>> output) {
  TSDelayFloat sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<float> generator(input);

  AssertEqualsTimed<float> assertions(output);
}
