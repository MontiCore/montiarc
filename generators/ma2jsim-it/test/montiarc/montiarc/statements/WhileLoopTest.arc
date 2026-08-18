/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test, input=[Untimed<1>, Untimed<2>], expected=[
  Untimed<0, 1, 2, 3, 4, 5, 6, 7, 8, 9>,
  Untimed<0, 1, 2, 3, 4, 5, 6, 7, 8, 9>
]>>
component WhileLoopTest(UntimedStream<Integer> input, UntimedStream<Integer> expected) {
  WhileLoop sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitList<Integer> generator(input);

  AssertEqualsUntimed<Integer> assertions(expected);
}
