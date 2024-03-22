/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamBool5(boolean p) {

  port in boolean i;
  port out boolean o;

  // boolean v = p;

  automaton {
    initial state S;

    S -> S [p] i / {
      if (p) {
        o = p;
      }
    };
  }
}
