/* (c) https://github.com/MontiCore/monticore */
package simple;

import simple.Types.AType;

component AtomicComp {
  port out int p1;
  port out int p2;
  port out AType p3;
  port out Integer p4;
  port in int p;

  automaton {
    initial state A;
    state B;

    A -> B / {
      int i = 0 + 3;
      String s = "ads" + 1;
      if (i > 2) {
        p1 = i;
      }
    }
    A -> B p;
    A -> B;
  }
}
