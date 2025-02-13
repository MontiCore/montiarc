/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import java.util.Set;

component SetIntersection {

  port sync in Set<Integer> a;
  port sync in Set<Integer> b;
  port sync out Set<Integer> out;

  automaton {
    initial state S;

    S -> S / {
      out = a intersect b;
    };
  }
}
