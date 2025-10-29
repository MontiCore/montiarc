/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import montiarc.types.CardinalDirection;
import montiarc.types.CardinalDirection.*;
import montiarc.types.Signal;

component List6() {
  port in Signal i;
  port out CardinalDirection o;

  List<CardinalDirection> l = [ ];

  automaton {
    initial state Empty;
    state Full;

    Empty -> Full / {
      //l.addAll([NORTH, EAST, SOUTH, WEST]);
    }
    Full -> Full [l.size() > 1] i / {
      o = l.get(l.size());
    }
    Full -> Empty [l.size() == 1] i / {
      o = l.get(l.size());
    }
  }

}
