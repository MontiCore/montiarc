/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.lang.Signal;

component ForEachWithList2 {

  port in Signal i;
  port out int o;

  automaton {
    initial state S;
    S -> S i / {
      for (int e : [0, 1]) {
        o = e;
      }
    }
  }
}
