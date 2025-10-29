/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import montiarc.types.CardinalDirection;
import montiarc.types.Signal;

component List1 {
  port in CardinalDirection i;
  port in Signal i2;
  port out CardinalDirection o;

  List<CardinalDirection> l = [ ];

  automaton {
    initial state S;
    S -> S i / {
      l.add(i);
    }
    S -> S [l.size() > 0] i2 / {
      o = l.remove(l.size() - 1);
    }
  }

}
