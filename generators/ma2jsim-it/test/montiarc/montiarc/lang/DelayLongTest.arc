/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;
import java.lang.Long;

<<test, ticks=[1,1,1,1,1,1,2,2,2,2], input=[
  [[Long.MIN_VALUE]],
  [[Long.MAX_VALUE]],
  [[Long.MIN_VALUE, Long.MIN_VALUE]],
  [[Long.MIN_VALUE, Long.MAX_VALUE]],
  [[Long.MAX_VALUE, Long.MIN_VALUE]],
  [[Long.MAX_VALUE, Long.MAX_VALUE]],
  [[Long.MIN_VALUE], [Long.MIN_VALUE]],
  [[Long.MIN_VALUE], [Long.MAX_VALUE]],
  [[Long.MAX_VALUE], [Long.MIN_VALUE]],
  [[Long.MAX_VALUE], [Long.MAX_VALUE]]
], output=[
  [[], [Long.MIN_VALUE]],
  [[], [Long.MAX_VALUE]],
  [[], [Long.MIN_VALUE, Long.MIN_VALUE]],
  [[], [Long.MIN_VALUE, Long.MAX_VALUE]],
  [[], [Long.MAX_VALUE, Long.MIN_VALUE]],
  [[], [Long.MAX_VALUE, Long.MAX_VALUE]],
  [[], [Long.MIN_VALUE], [Long.MIN_VALUE]],
  [[], [Long.MIN_VALUE], [Long.MAX_VALUE]],
  [[], [Long.MAX_VALUE], [Long.MIN_VALUE]],
  [[], [Long.MAX_VALUE], [Long.MAX_VALUE]]
]>>
component DelayLongTest(List<List<long>> input, List<List<long>> output) {
  DelayLong sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<long> generator(input);

  AssertEqualsTimed<long> assertions(output);
}
