/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

component ParamsBool3(boolean p1, boolean p2) {

  port in boolean i;
  port out boolean o1, o2;

  automaton {
    initial state S;

    S -> S [p1 && p2] i / {
      o1 = i;
      o2 = i;
    };

    S -> S [p1 && !p2] i / {
      o1 = i;
    };

    S -> S [!p1 && p2] i / {
      o2 = i;
    };

    S -> S [!p1 && !p2] i / { };
  }
}

