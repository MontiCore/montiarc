/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;


<<test, p1=[
  true, true, true, true, false, false, false, false, true, true, false, false
], p2=[
  true, true, false, false, true, true, false, false, true, false, true, false
], input=[
  Untimed<true>, Untimed<false>, Untimed<true>, Untimed<false>, Untimed<true>, Untimed<false>, Untimed<true>, Untimed<false>,
  Untimed<true, true>, Untimed<true, true>, Untimed<true, true>, Untimed<true, true>
], expected1=[
  Untimed<true>, Untimed<true>, Untimed<true>, Untimed<true>, Untimed<false>, Untimed<false>, Untimed<false>, Untimed<false>,
  Untimed<true, true>, Untimed<true, true>, Untimed<false, false>, Untimed<false, false>
], expected2=[
  Untimed<true>, Untimed<true>, Untimed<false>, Untimed<false>, Untimed<true>, Untimed<true>, Untimed<false>, Untimed<false>,
  Untimed<true, true>, Untimed<false, false>, Untimed<true, true>, Untimed<false, false>
]>>
component ParamsBool1Test(boolean p1, boolean p2, UntimedStream<boolean> input, UntimedStream<boolean> expected1, UntimedStream<boolean> expected2) {
  ParamsBool1 sut(p1, p2);

  generator.out -> sut.i;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitList<boolean> generator(input);

  AssertEqualsUntimed<boolean> assertions1(expected1), assertions2(expected2);
}
