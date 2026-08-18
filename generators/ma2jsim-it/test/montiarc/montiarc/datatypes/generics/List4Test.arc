/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;
import montiarc.types.CardinalDirection;
import montiarc.types.CardinalDirection.*;

<<test, i=[Untimed<Integer><>, Untimed<0>, Untimed<-1>, Untimed<1>, Untimed<-2147483648>, Untimed<2147483647>, Untimed<-1, 0, 1>], o=[Untimed<CardinalDirection><>, Untimed<NORTH>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<NORTH>]>>
component List4Test(UntimedStream<Integer> i, UntimedStream<CardinalDirection> o) {
  List4 sut;

  generator.out -> sut.i;
  sut.o -> assert.actual;

  EmitList<Integer> generator(i);

  AssertEqualsUntimed<CardinalDirection> assert(o);
}
