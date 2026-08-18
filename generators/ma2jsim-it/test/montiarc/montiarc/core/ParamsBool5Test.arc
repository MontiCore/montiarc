/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test={
  [true, true, Untimed<true>, Untimed<true>],
  [true, true, Untimed<false>, Untimed<true>],
  [true, false, Untimed<true>, Untimed<boolean><>],
  [true, false, Untimed<false>, Untimed<boolean><>],
  [false, true, Untimed<true>, Untimed<boolean><>],
  [false, true, Untimed<false>, Untimed<boolean><>],
  [false, false, Untimed<true>, Untimed<boolean><>],
  [false, false, Untimed<false>, Untimed<boolean><>],
  [true, true, Untimed<true, true>, Untimed<true, true>],
  [true, true, Untimed<true, false>, Untimed<true, true>],
  [true, true, Untimed<false, true>, Untimed<true, true>],
  [true, true, Untimed<false, false>, Untimed<true, true>],
  [true, false, Untimed<true, true>, Untimed<boolean><>],
  [true, false, Untimed<true, false>, Untimed<boolean><>],
  [true, false, Untimed<false, true>, Untimed<boolean><>],
  [true, false, Untimed<false, false>, Untimed<boolean><>],
  [false, true, Untimed<true, true>, Untimed<boolean><>],
  [false, true, Untimed<true, false>, Untimed<boolean><>],
  [false, true, Untimed<false, true>, Untimed<boolean><>],
  [false, true, Untimed<false, false>, Untimed<boolean><>],
  [false, false, Untimed<true, true>, Untimed<boolean><>],
  [false, false, Untimed<true, false>, Untimed<boolean><>],
  [false, false, Untimed<false, true>, Untimed<boolean><>],
  [false, false, Untimed<false, false>, Untimed<boolean><>]
}>>
component ParamsBool5Test(boolean p1, boolean p2, UntimedStream<boolean> input, UntimedStream<boolean> expected) {
  ParamsBool5 sut(p1, p2);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitList<boolean> generator(input);

  AssertEqualsUntimed<boolean> assertions(expected);
}
