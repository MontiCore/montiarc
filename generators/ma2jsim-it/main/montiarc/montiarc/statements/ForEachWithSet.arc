/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import java.util.Set;
import montiarc.types.OnOff;

component ForEachWithSet {

  port in Set<OnOff> i;
  port out OnOff o;

  automaton {
    initial state S;
    S -> S i / {
      for (OnOff e : i) {
        o = e;
      }
    }
  }
}
