/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import java.util.List;

component ForEachWithList1 {

  port in List<int> i;
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
