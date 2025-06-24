/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Short;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Short.MIN_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Short.MIN_VALUE, Short.MIN_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE],
input=[
  [Short.MIN_VALUE],
  [Short.MAX_VALUE],
  [Short.MIN_VALUE],
  [Short.MAX_VALUE],
  [Short.MIN_VALUE, Short.MIN_VALUE],
  [Short.MIN_VALUE, Short.MAX_VALUE],
  [Short.MAX_VALUE, Short.MIN_VALUE],
  [Short.MAX_VALUE, Short.MAX_VALUE],
  [Short.MIN_VALUE, Short.MIN_VALUE],
  [Short.MIN_VALUE, Short.MAX_VALUE],
  [Short.MAX_VALUE, Short.MIN_VALUE],
  [Short.MAX_VALUE, Short.MAX_VALUE]
], output=[
  [[Short.MIN_VALUE]],
  [[Short.MIN_VALUE]],
  [[Short.MAX_VALUE]],
  [[Short.MAX_VALUE]],
  [[Short.MIN_VALUE], [Short.MIN_VALUE]],
  [[Short.MIN_VALUE], [Short.MIN_VALUE]],
  [[Short.MIN_VALUE], [Short.MAX_VALUE]],
  [[Short.MIN_VALUE], [Short.MAX_VALUE]],
  [[Short.MAX_VALUE], [Short.MIN_VALUE]],
  [[Short.MAX_VALUE], [Short.MIN_VALUE]],
  [[Short.MAX_VALUE], [Short.MAX_VALUE]],
  [[Short.MAX_VALUE], [Short.MAX_VALUE]]
]>>
component TSDelayShortTest(short init, List<short> input, List<List<short>> output) {
  TSDelayShort sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<short> generator(input);

  AssertEqualsTimed<short> assertions(output);
}
