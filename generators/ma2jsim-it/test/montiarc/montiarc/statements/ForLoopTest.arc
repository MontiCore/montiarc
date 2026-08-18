/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test, input=[Untimed<1>, Untimed<2>, Untimed<3>, Untimed<4>, Untimed<5>, Untimed<6>], expected=[
  Untimed<0, 1, 2, 3, 4, 5, 6, 7, 8, 9>,
  Untimed<9, 8, 7, 6, 5, 4, 3, 2, 1, 0>,
  Untimed<2, 4, 8, 16, 32, 64, 128, 256, 512, 1024>,
  Untimed<0, 1, 2, 3, 4, 5, 6, 7, 8, 9>,
  Untimed<0, 1, 2, 3, 4, 5, 6, 7, 8, 9>,
  Untimed<0, 1, 2, 3, 4, 5, 6, 7, 8, 9>
]>>
component ForLoopTest(UntimedStream<Integer> input, UntimedStream<Integer> expected) {
  ForLoop sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitList<Integer> generator(input);

  AssertEqualsUntimed<Integer> assertions(expected);
}
