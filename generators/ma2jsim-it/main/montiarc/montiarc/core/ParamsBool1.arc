/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamsBool1(boolean p1, boolean p2) {

  port in boolean i;
  port out boolean o1, o2;

  automaton {
    initial state S;

    S -> S i / {
      o1 = p1;
      o2 = p2;
    };
  }
}

