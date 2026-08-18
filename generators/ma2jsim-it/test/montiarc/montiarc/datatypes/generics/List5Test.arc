/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;
import montiarc.types.CardinalDirection;
import montiarc.types.CardinalDirection.*;

<<test, i=[Untimed<Integer><>, Untimed<0>, Untimed<-1>, Untimed<1>, Untimed<2>, Untimed<3>, Untimed<-2147483648>, Untimed<2147483647>, Untimed<-1, 0, 1, 2, 3>], o0=[Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>], o1=[Untimed<CardinalDirection><>, Untimed<NORTH>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<NORTH>], o2=[Untimed<CardinalDirection><>, Untimed<NORTH>, Untimed<CardinalDirection><>, Untimed<EAST>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<NORTH, EAST>], o3=[Untimed<CardinalDirection><>, Untimed<NORTH>, Untimed<CardinalDirection><>, Untimed<EAST>, Untimed<SOUTH>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<NORTH, EAST, SOUTH>], o4=[Untimed<CardinalDirection><>, Untimed<NORTH>, Untimed<CardinalDirection><>, Untimed<EAST>, Untimed<SOUTH>, Untimed<WEST>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<NORTH, EAST, SOUTH, WEST>]>>
component List5Test(UntimedStream<Integer> i, UntimedStream<CardinalDirection> o0, UntimedStream<CardinalDirection> o1, UntimedStream<CardinalDirection> o2, UntimedStream<CardinalDirection> o3, UntimedStream<CardinalDirection> o4) {
  List5 sut;

  generator.out -> sut.i;
  sut.o0 -> a0.actual;
  sut.o1 -> a1.actual;
  sut.o2 -> a2.actual;
  sut.o3 -> a3.actual;
  sut.o4 -> a4.actual;

  EmitList<Integer> generator(i);

  AssertEqualsUntimed<CardinalDirection> a0(o0), a1(o1), a2(o2), a3(o3), a4(o4);
}
