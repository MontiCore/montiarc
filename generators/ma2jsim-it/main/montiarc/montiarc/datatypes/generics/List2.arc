/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import java.util.List;
import montiarc.types.CardinalDirection;

component List2(List<CardinalDirection> p) {
  port in Integer i;
  port out CardinalDirection o;

  List<CardinalDirection> l = p;

  automaton {
    initial state S;
    S -> S [i >= 0 && l.size() > i] i / {
      o = l.get(i);
    };
  }

}
