/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import java.lang.Iterable;
import java.lang.String;
import java.util.List;
import java.util.Set;
import montiarc.types.IntSeq;

component ForEachLoop {

  port in int i;
  port in List<int> list;
  port in Set<int> set;
  port in Iterable<int> iter;
  port in IntSeq obj;
  port in String str;
  port out int o;
  port out char c;

  automaton {
    initial state S;
    S -> S list / {
      for (int e : list) {
        o = e;
      }
    }
    S -> S set / {
      for (int e : set) {
        o = e;
      }
    }
    S -> S iter / {
      for (int e : iter) {
        o = e;
      }
    }
    S -> S obj / {
      for (int e : obj) {
        o = e;
      }
    }
    S -> S str / {
      for (char e : str) {
        c = e;
      }
    }
    S -> S [i == 1] i / {
      for (int e : [0, 1]) {
        o = e;
      }
    }
    S -> S [i == 2] i / {
      for (int e : {2, 3}) {
        o = e;
      }
    }
  }
}
