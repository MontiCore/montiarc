/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test, p=[
  true, true, false, false, true, true, true, true, false, false, false, false
], input=[
  Untimed<true>, Untimed<false>, Untimed<true>, Untimed<false>,
  Untimed<true, true>, Untimed<true, false>, Untimed<false, true>, Untimed<false, false>,
  Untimed<true, true>, Untimed<true, false>, Untimed<false, true>, Untimed<false, false>
]>>
component ParamBool3Test(boolean p, UntimedStream<boolean> input) {
  ParamBool3 sut(p);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitList<boolean> generator(input);

  AssertEqualsUntimed<boolean> assertions(input);
}
