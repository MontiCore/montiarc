/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test, input=[Untimed<1>, Untimed<2>, Untimed<3>, Untimed<4>, Untimed<5>, Untimed<-1>], expected=[Untimed<1>, Untimed<2>, Untimed<3>, Untimed<4>, Untimed<5>, Untimed<-1>]>>
component IfElseConditionalTest(UntimedStream<Integer> input, UntimedStream<Integer> expected) {
  IfElseConditional sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitList<Integer> generator(input);

  AssertEqualsUntimed<Integer> assertions(expected);
}
