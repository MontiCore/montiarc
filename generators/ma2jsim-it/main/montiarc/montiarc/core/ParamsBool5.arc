/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamsBool5(boolean p1, boolean p2) {

  port in boolean i;
  port out boolean o;

  // boolean v1 = p1;
  // boolean v2 = p2;

  automaton {
    initial state S;

    S -> S [p1 && p2] i / {
      if (p1 && p2) {
        o = p1 && p2;
      }
    };
  }
}
