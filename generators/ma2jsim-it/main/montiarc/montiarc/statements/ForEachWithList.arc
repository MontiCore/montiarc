/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import java.util.List;
import montiarc.types.OnOff;

component ForEachWithList {

  port in List<OnOff> i;
  port out OnOff o;

  automaton {
    initial state S;
    S -> S i / {
      for (OnOff e : i) {
        o = e;
      }
    };
  }
}
