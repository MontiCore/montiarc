/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamsBool2(boolean p1, boolean p2) {

  port in boolean i;
  port out boolean o1, o2;

  // boolean v1 = p1;
  // boolean v2 = p2;

  automaton {
    initial state S;
  }
}

