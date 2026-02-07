/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import java.util.Set;

component ForEachWithSet1 {

  port in Set<int> i;
  port out int o;

  automaton {
    initial state S;
    S -> S i / {
      for (int e : i) {
        o = e;
      }
    }
  }
}
