/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamBool1(boolean p) {

  port in boolean i;
  port out boolean o;

  automaton {
    initial state S;

    S -> S i / {
      o = p;
    };
  }
}
