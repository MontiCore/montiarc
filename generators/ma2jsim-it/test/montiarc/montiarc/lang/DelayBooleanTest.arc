/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  [[true]],
  [[false]],
  [[true, true]],
  [[true, false]],
  [[false, true]],
  [[false, false]],
  [[true], [true]],
  [[true], [false]],
  [[false], [true]],
  [[false], [false]]
], output=[
  [[], [true]],
  [[], [false]],
  [[], [true, true]],
  [[], [true, false]],
  [[], [false, true]],
  [[], [false, false]],
  [[], [true], [true]],
  [[], [true], [false]],
  [[], [false], [true]],
  [[], [false], [false]]
]>>
component DelayBooleanTest(List<List<boolean>> input, List<List<boolean>> output) {
  DelayBoolean sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<boolean> generator(input);

  AssertEqualsTimed<boolean> assertions(output);
}
