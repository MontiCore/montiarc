/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamBool3(boolean p) {

  port in boolean i;
  port out boolean o;

  automaton {
    initial state S;

    S -> S [p] i / {
      o = i;
    };
  }
}
