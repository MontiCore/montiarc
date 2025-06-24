/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Double;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE],
input=[
  [Double.MIN_VALUE],
  [Double.MAX_VALUE],
  [Double.MIN_VALUE],
  [Double.MAX_VALUE],
  [Double.MIN_VALUE, Double.MIN_VALUE],
  [Double.MIN_VALUE, Double.MAX_VALUE],
  [Double.MAX_VALUE, Double.MIN_VALUE],
  [Double.MAX_VALUE, Double.MAX_VALUE],
  [Double.MIN_VALUE, Double.MIN_VALUE],
  [Double.MIN_VALUE, Double.MAX_VALUE],
  [Double.MAX_VALUE, Double.MIN_VALUE],
  [Double.MAX_VALUE, Double.MAX_VALUE]
], output=[
  [[Double.MIN_VALUE]],
  [[Double.MIN_VALUE]],
  [[Double.MAX_VALUE]],
  [[Double.MAX_VALUE]],
  [[Double.MIN_VALUE], [Double.MIN_VALUE]],
  [[Double.MIN_VALUE], [Double.MIN_VALUE]],
  [[Double.MIN_VALUE], [Double.MAX_VALUE]],
  [[Double.MIN_VALUE], [Double.MAX_VALUE]],
  [[Double.MAX_VALUE], [Double.MIN_VALUE]],
  [[Double.MAX_VALUE], [Double.MIN_VALUE]],
  [[Double.MAX_VALUE], [Double.MAX_VALUE]],
  [[Double.MAX_VALUE], [Double.MAX_VALUE]]
]>>
component TSDelayDoubleTest(double init, List<double> input, List<List<double>> output) {
  TSDelayDouble sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<double> generator(input);

  AssertEqualsTimed<double> assertions(output);
}
