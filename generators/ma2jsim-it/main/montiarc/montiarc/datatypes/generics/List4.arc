/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import montiarc.types.CardinalDirection;
import montiarc.types.CardinalDirection.*;

component List4() {
  port in Integer i;
  port out CardinalDirection o;

  List2 l([NORTH]);
  i -> l.i;
  l.o -> o;

}
