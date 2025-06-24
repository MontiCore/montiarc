/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[true, true, false, false, true, true, true, true, false, false, false, false],
input=[
  [true],
  [false],
  [true],
  [false],
  [true, true],
  [true, false],
  [false, true],
  [false, false],
  [true, true],
  [true, false],
  [false, true],
  [false, false]
], output=[
  [[true]],
  [[true]],
  [[false]],
  [[false]],
  [[true], [true]],
  [[true], [true]],
  [[true], [false]],
  [[true], [false]],
  [[false], [true]],
  [[false], [true]],
  [[false], [false]],
  [[false], [false]]
]>>
component TSDelayBooleanTest(boolean init, List<boolean> input, List<List<boolean>> output) {
  TSDelayBoolean sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<boolean> generator(input);

  AssertEqualsTimed<boolean> assertions(output);
}
