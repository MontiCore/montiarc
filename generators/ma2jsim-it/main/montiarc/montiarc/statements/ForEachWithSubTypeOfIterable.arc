/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.types.ItInt;

component ForEachWithSubTypeOfIterable {

  port in ItInt i;
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
