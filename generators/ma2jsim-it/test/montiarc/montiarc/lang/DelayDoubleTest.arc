/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;
import java.lang.Double;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  [[Double.MIN_VALUE]],
  [[Double.MAX_VALUE]],
  [[Double.MIN_VALUE, Double.MIN_VALUE]],
  [[Double.MIN_VALUE, Double.MAX_VALUE]],
  [[Double.MAX_VALUE, Double.MIN_VALUE]],
  [[Double.MAX_VALUE, Double.MAX_VALUE]],
  [[Double.MIN_VALUE], [Double.MIN_VALUE]],
  [[Double.MIN_VALUE], [Double.MAX_VALUE]],
  [[Double.MAX_VALUE], [Double.MIN_VALUE]],
  [[Double.MAX_VALUE], [Double.MAX_VALUE]]
], output=[
  [[], [Double.MIN_VALUE]],
  [[], [Double.MAX_VALUE]],
  [[], [Double.MIN_VALUE, Double.MIN_VALUE]],
  [[], [Double.MIN_VALUE, Double.MAX_VALUE]],
  [[], [Double.MAX_VALUE, Double.MIN_VALUE]],
  [[], [Double.MAX_VALUE, Double.MAX_VALUE]],
  [[], [Double.MIN_VALUE], [Double.MIN_VALUE]],
  [[], [Double.MIN_VALUE], [Double.MAX_VALUE]],
  [[], [Double.MAX_VALUE], [Double.MIN_VALUE]],
  [[], [Double.MAX_VALUE], [Double.MAX_VALUE]]
]>>
component DelayDoubleTest(List<List<double>> input, List<List<double>> output) {
  DelayDouble sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<double> generator(input);

  AssertEqualsTimed<double> assertions(output);
}
