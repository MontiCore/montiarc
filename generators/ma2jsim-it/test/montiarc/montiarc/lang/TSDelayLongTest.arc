/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Long;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE],
input=[
  [Long.MIN_VALUE],
  [Long.MAX_VALUE],
  [Long.MIN_VALUE],
  [Long.MAX_VALUE],
  [Long.MIN_VALUE, Long.MIN_VALUE],
  [Long.MIN_VALUE, Long.MAX_VALUE],
  [Long.MAX_VALUE, Long.MIN_VALUE],
  [Long.MAX_VALUE, Long.MAX_VALUE],
  [Long.MIN_VALUE, Long.MIN_VALUE],
  [Long.MIN_VALUE, Long.MAX_VALUE],
  [Long.MAX_VALUE, Long.MIN_VALUE],
  [Long.MAX_VALUE, Long.MAX_VALUE]
], output=[
  [[Long.MIN_VALUE]],
  [[Long.MIN_VALUE]],
  [[Long.MAX_VALUE]],
  [[Long.MAX_VALUE]],
  [[Long.MIN_VALUE], [Long.MIN_VALUE]],
  [[Long.MIN_VALUE], [Long.MIN_VALUE]],
  [[Long.MIN_VALUE], [Long.MAX_VALUE]],
  [[Long.MIN_VALUE], [Long.MAX_VALUE]],
  [[Long.MAX_VALUE], [Long.MIN_VALUE]],
  [[Long.MAX_VALUE], [Long.MIN_VALUE]],
  [[Long.MAX_VALUE], [Long.MAX_VALUE]],
  [[Long.MAX_VALUE], [Long.MAX_VALUE]]
]>>
component TSDelayLongTest(long init, List<long> input, List<List<long>> output) {
  TSDelayLong sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<long> generator(input);

  AssertEqualsTimed<long> assertions(output);
}
