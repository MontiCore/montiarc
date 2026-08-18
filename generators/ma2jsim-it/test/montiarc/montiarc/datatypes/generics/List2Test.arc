/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import montiarc.types.CardinalDirection;
import montiarc.types.CardinalDirection.*;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;

<<test, p=[
  [], [], [],
  [NORTH], [NORTH], [NORTH], [NORTH], [NORTH],
  [NORTH, EAST], [NORTH, EAST],
  [NORTH, EAST, SOUTH, WEST], [NORTH, EAST, SOUTH, WEST], [NORTH, EAST, SOUTH, WEST], [NORTH, EAST, SOUTH, WEST], [NORTH, EAST, SOUTH, WEST], [NORTH, EAST, SOUTH, WEST]
], i=[
  Untimed<0>, Untimed<-2147483648>, Untimed<2147483647>,
  Untimed<Integer><>, Untimed<0>, Untimed<-1>, Untimed<1>, Untimed<0, 0>,
  Untimed<0, 1>, Untimed<1, 0>,
  Untimed<0, 1, 2, 3>, Untimed<0, 1, 3, 2>, Untimed<0, 2, 1, 3>, Untimed<0, 2, 3, 1>, Untimed<0, 3, 1, 2>, Untimed<0, 3, 2, 1>
], o=[
  Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>,
  Untimed<CardinalDirection><>, Untimed<NORTH>, Untimed<CardinalDirection><>, Untimed<CardinalDirection><>, Untimed<NORTH, NORTH>,
  Untimed<NORTH, EAST>, Untimed<EAST, NORTH>,
  Untimed<NORTH, EAST, SOUTH, WEST>, Untimed<NORTH, EAST, WEST, SOUTH>, Untimed<NORTH, SOUTH, EAST, WEST>, Untimed<NORTH, SOUTH, WEST, EAST>, Untimed<NORTH, WEST, EAST, SOUTH>, Untimed<NORTH, WEST, SOUTH, EAST>
]>>
component List2Test(List<CardinalDirection> p, UntimedStream<Integer> i, UntimedStream<CardinalDirection> o) {
  List2 sut(p);

  generator.out -> sut.i;
  sut.o -> assert.actual;

  EmitList<Integer> generator(i);

  AssertEqualsUntimed<CardinalDirection> assert(o);
}
