/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;
import java.lang.Number;
import java.lang.Integer;

<<test={
  [Untimed<0, 1, 2>, Untimed<Number><1.0f, 3.33333, 1.6f>],
  [Untimed<-1, 3, Integer.MAX_VALUE>, Untimed<Number><>]
}>>
component NumberListTest(UntimedStream<Integer> i, UntimedStream<Number> o) {
  NumberList sut;

  generator.out -> sut.i;
  sut.o -> assert.actual;

  EmitList<Integer> generator(i);

  AssertEqualsUntimed<Number> assert(o);
}
