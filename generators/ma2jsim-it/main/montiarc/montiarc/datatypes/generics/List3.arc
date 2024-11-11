/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import montiarc.types.CardinalDirection;

component List3() {
  port in Integer i;
  port out CardinalDirection o;

  List2 l([]);
  i -> l.i;
  l.o -> o;

}
