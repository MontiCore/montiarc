/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamsBool4(boolean p1, boolean p2) {

  port in boolean i;
  port out boolean o1, o2;

  automaton {
    initial state S;

    S -> S i / {
      if (p1 && p2) {
        o1 = i;
        o2 = i;
      } else if (p1 && !p2) {
        o1 = i;
      } else if (!p1 && p2) {
        o2 = i;
      } else if (!p1 && !p2) {

      }
    };
  }
}

