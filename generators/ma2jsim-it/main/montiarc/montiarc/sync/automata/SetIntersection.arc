/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import java.util.Set;

component SetIntersection {

  port in Set<Integer> a;
  port in Set<Integer> b;
  port out Set<Integer> out;

  <<sync>> automaton {
    initial state S;

    S -> S / {
      out = a intersect b;
    };
  }
}
