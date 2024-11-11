/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import montiarc.types.CardinalDirection;
import montiarc.types.CardinalDirection.*;

component List5() {
  port in Integer i;
  port out CardinalDirection o0;
  port out CardinalDirection o1;
  port out CardinalDirection o2;
  port out CardinalDirection o3;
  port out CardinalDirection o4;

  List2 l0([]);
  i -> l0.i;
  l0.o -> o0;

  List2 l1([NORTH]);
  i -> l1.i;
  l1.o -> o1;

  List2 l2([NORTH, EAST]);
  i -> l2.i;
  l2.o -> o2;

  List2 l3([NORTH, EAST, SOUTH]);
  i -> l3.i;
  l3.o -> o3;

  List2 l4([NORTH, EAST, SOUTH, WEST]);
  i -> l4.i;
  l4.o -> o4;
}
