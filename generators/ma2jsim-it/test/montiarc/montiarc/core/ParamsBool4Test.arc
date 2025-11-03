/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test={
  [true, true, Untimed<true>, Untimed<true>, Untimed<true>],
  [true, true, Untimed<false>, Untimed<false>, Untimed<false>],
  [true, false, Untimed<true>, Untimed<true>, Untimed<boolean><>],
  [true, false, Untimed<false>, Untimed<false>, Untimed<boolean><>],
  [false, true, Untimed<true>, Untimed<boolean><>, Untimed<true>],
  [false, true, Untimed<false>, Untimed<boolean><>, Untimed<false>],
  [false, false, Untimed<true>, Untimed<boolean><>, Untimed<boolean><>],
  [false, false, Untimed<false>, Untimed<boolean><>, Untimed<boolean><>],
  [true, true, Untimed<true, true>, Untimed<true, true>, Untimed<true, true>],
  [true, true, Untimed<true, false>, Untimed<true, false>, Untimed<true, false>],
  [true, true, Untimed<false, true>, Untimed<false, true>, Untimed<false, true>],
  [true, true, Untimed<false, false>, Untimed<false, false>, Untimed<false, false>],
  [true, false, Untimed<true, true>, Untimed<true, true>, Untimed<boolean><>],
  [true, false, Untimed<true, false>, Untimed<true, false>, Untimed<boolean><>],
  [true, false, Untimed<false, true>, Untimed<false, true>, Untimed<boolean><>],
  [true, false, Untimed<false, false>, Untimed<false, false>, Untimed<boolean><>],
  [false, true, Untimed<true, true>, Untimed<boolean><>, Untimed<true, true>],
  [false, true, Untimed<true, false>, Untimed<boolean><>, Untimed<true, false>],
  [false, true, Untimed<false, true>, Untimed<boolean><>, Untimed<false, true>],
  [false, true, Untimed<false, false>, Untimed<boolean><>, Untimed<false, false>],
  [false, false, Untimed<true, true>, Untimed<boolean><>, Untimed<boolean><>],
  [false, false, Untimed<true, false>, Untimed<boolean><>, Untimed<boolean><>],
  [false, false, Untimed<false, true>, Untimed<boolean><>, Untimed<boolean><>],
  [false, false, Untimed<false, false>, Untimed<boolean><>, Untimed<boolean><>]
}>>
component ParamsBool4Test(boolean p1, boolean p2, UntimedStream<boolean> input, UntimedStream<boolean> expected1, UntimedStream<boolean> expected2) {
  ParamsBool4 sut(p1, p2);

  generator.out -> sut.i;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitList<boolean> generator(input);

  AssertEqualsUntimed<boolean> assertions1(expected1), assertions2(expected2);
}
